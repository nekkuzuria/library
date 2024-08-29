import { Component, NgZone, inject, OnInit } from '@angular/core';
import { IBook } from '../entities/book/book.model';
import { EntityArrayResponseType, BookService } from '../entities/book/service/book.service';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { HttpHeaders } from '@angular/common/http';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { Genre } from '../entities/enumerations/genre.model';
import { NgSelectComponent, NgOptionTemplateDirective, NgLabelTemplateDirective } from '@ng-select/ng-select';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DashboardService } from './dashboard.service';
import { IPersonalStorage } from '../visitor-pages/personal-storage/personal-storage.model';
import { PersonalStorageService } from '../visitor-pages/personal-storage/personal-storage.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [NgSelectComponent, NgOptionTemplateDirective, NgLabelTemplateDirective, CommonModule, FormsModule, RouterModule],
})
export class DashboardComponent implements OnInit {
  books?: IBook[];
  genres: string[] = [];
  filteredBooks: IBook[] = [];
  searchQuery: string = '';
  selectedGenre: string | null = null;
  sortOrder: 'asc' | 'desc' = 'asc';
  visitorBookStorages?: IPersonalStorage[];
  genreCounts: { [genre: string]: number } = {};
  sortedGenres: { name: string; count: number }[] = [];

  totalItems = 0;
  isLoading = false;
  itemsPerPage = ITEMS_PER_PAGE;
  sortState = sortStateSignal({});
  page = 1;
  sortOption: string | null = null;

  protected sortService = inject(SortService);

  sortOptions = [
    { value: 'title', label: 'Sort by Title' },
    { value: 'author', label: 'Sort by Author' },
    { value: 'year', label: 'Sort by Year' },
  ];

  constructor(
    private personalStorageService: PersonalStorageService,
    private bookService: BookService,
  ) {}

  trackId = (_index: number, item: IBook): number => this.bookService.getBookIdentifier(item);
  ngOnInit(): void {
    this.load();
    this.loadCurrentUserBookStorage();
    this.genres = Object.values(Genre).map(genre => {
      return genre
        .toLowerCase()
        .replace(/_/g, ' ')
        .replace(/\b\w/g, char => char.toUpperCase());
    });
  }

  loadCurrentUserBookStorage(): void {
    this.personalStorageService.query().subscribe(response => {
      this.visitorBookStorages = response.body || [];
      this.visitorBookStorages = this.visitorBookStorages.filter(vbs => vbs.returnDate !== null);
    });
    this.countGenres();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  onSearch(): void {
    const query = this.searchQuery.toLowerCase();

    this.filteredBooks = (this.books ?? []).filter(book => {
      const matchesSearchQuery = query ? book.title?.toLowerCase().includes(query) || book.author?.toLowerCase().includes(query) : true;
      const matchesGenre = this.selectedGenre!.length > 0 ? this.selectedGenre!.includes(book.genre!) : true;
      return matchesSearchQuery && matchesGenre;
    });

    this.sortBooks();
  }

  sortBooks(): void {
    if (!this.sortOption) return;
    const sortValue =
      typeof this.sortOption === 'string'
        ? this.sortOption
        : this.sortOption !== null
          ? (this.sortOption as { value: string }).value
          : null;
    if (sortValue === 'title') {
      this.filteredBooks.sort((a, b) => {
        const titleA = a.title ?? '';
        const titleB = b.title ?? '';
        return this.sortOrder === 'asc' ? titleA.localeCompare(titleB) : titleB.localeCompare(titleA);
      });
    } else if (sortValue === 'author') {
      this.filteredBooks.sort((a, b) => {
        const authorA = a.author ?? '';
        const authorB = b.author ?? '';
        return this.sortOrder === 'asc' ? authorA.localeCompare(authorB) : authorB.localeCompare(authorA);
      });
    } else if (sortValue === 'year') {
      this.filteredBooks.sort((a, b) => {
        const yearA = a.year ?? 0;
        const yearB = b.year ?? 0;
        return this.sortOrder === 'asc' ? yearA - yearB : yearB - yearA;
      });
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.books = dataFromBody;
    this.filteredBooks = this.books;
    console.log(this.filteredBooks);
    this.sortBooks();
    this.filteredBooks.forEach(book => {
      if (book.file && book.file.image) {
        book.cover = `data:image/jpeg;base64,${book.file.image}`;
      }
    });
  }

  protected fillComponentAttributesFromResponseBody(data: IBook[] | null): IBook[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.bookService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  setSortOrder(order: 'asc' | 'desc'): void {
    console.log(order);
    this.sortOrder = order;
    this.sortBooks();
  }

  private countGenres(): void {
    const genreCounts: { [key: string]: number } = {};
    console.log('AAAAAAAAAAAAAAAAA', this.visitorBookStorages);
    // Count occurrences
    this.visitorBookStorages!.forEach(book => {
      const genre = book.genre;
      console.log(genre);

      if (genre) {
        // Ensure genre is not null or undefined
        if (genreCounts[genre]) {
          genreCounts[genre]++;
        } else {
          genreCounts[genre] = 1;
        }
      }
    });
    // Create an array of genre objects with name and count
    this.sortedGenres = Object.entries(genreCounts)
      .map(([genre, count]) => ({ name: genre, count }))
      .sort((a, b) => {
        // Sort primarily by count in descending order
        if (b.count !== a.count) {
          return b.count - a.count;
        }
        // Sort secondarily by name in alphabetical order
        return a.name.localeCompare(b.name);
      });

    // Log sorted genres by frequency and name
    console.log('Sorted genres by frequency and name:', this.sortedGenres);
  }
}
