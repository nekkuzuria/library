import { Component, NgZone, inject, OnInit } from '@angular/core';
import { IBook } from '../entities/book/book.model';
import { EntityArrayResponseType, BookService } from '../entities/book/service/book.service';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { HttpHeaders } from '@angular/common/http';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { Genre } from '../entities/enumerations/genre.model';
import { NgSelectComponent, NgOptionTemplateDirective, NgLabelTemplateDirective, NgSelectModule } from '@ng-select/ng-select';
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
  imports: [
    NgSelectComponent,
    NgOptionTemplateDirective,
    NgLabelTemplateDirective,
    CommonModule,
    FormsModule,
    RouterModule,
    NgSelectModule,
  ],
})
export class DashboardComponent implements OnInit {
  books?: IBook[];
  genres: string[] = [];
  filteredBooks: IBook[] = [];
  searchQuery: string = '';
  selectedGenre: string[] = [];
  sortOrder: 'asc' | 'desc' | '' = '';
  visitorBookStorages?: IPersonalStorage[];
  genreCounts: { [genre: string]: number } = {};
  sortedGenres: { name: string; count: number }[] = [];
  personalStorages?: IPersonalStorage[];
  displayGenres: { name: string; disabled: boolean }[] = [];
  groupedGenres: { name: string; category: string }[] = [];

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
  }

  loadCurrentUserBookStorage(): void {
    this.displayGenres = this.getGenres();

    this.personalStorageService.query().subscribe(response => {
      this.visitorBookStorages = response.body || [];
      this.visitorBookStorages = this.visitorBookStorages.filter(vbs => vbs.returnDate !== null);
      this.countGenres();
    });
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

    this.filteredBooks = [...(this.books ?? [])];

    this.filteredBooks = (this.books ?? []).filter(book => {
      const matchesSearchQuery = query ? book.title?.toLowerCase().includes(query) || book.author?.toLowerCase().includes(query) : true;
      const matchesGenre = this.selectedGenre.length
        ? this.selectedGenre.map((genre: string) => genre.toLowerCase()).includes(book.genre?.toLowerCase() || '')
        : true;
      return matchesSearchQuery && matchesGenre;
    });

    this.sortBooks();
  }

  sortBooks(): void {
    if (!this.sortOption) {
      this.sortOrder = '';
      return;
    }

    if (!this.sortOrder) {
      this.sortOrder = 'asc';
    }

    const sortValue =
      typeof this.sortOption === 'string'
        ? this.sortOption
        : this.sortOption !== null
          ? (this.sortOption as { value: string }).value
          : null;
    console.log('sortOrder2', this.sortOrder);
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

    this.visitorBookStorages!.forEach(book => {
      const genre = book.genre;
      if (genre) {
        genreCounts[genre] = (genreCounts[genre] || 0) + 1;
      }
    });

    this.sortedGenres = Object.entries(genreCounts)
      .map(([genre, count]) => ({
        name: genre
          .toLowerCase()
          .replace(/_/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase()),
        count,
      }))
      .sort((a, b) => {
        if (b.count !== a.count) {
          return b.count - a.count;
        }
        return a.name.localeCompare(b.name);
      });

    this.displayGenres = [];

    this.displayGenres.push({ name: '--- Most Borrowed ---', disabled: true });
    this.displayGenres.push(...this.sortedGenres.map(genre => ({ name: genre.name, disabled: false })));

    const otherGenres = this.getGenres().filter(genre => !this.sortedGenres.some(g => g.name.toLowerCase() === genre.name.toLowerCase()));

    this.displayGenres.push({ name: '--- Other Genres ---', disabled: true });
    this.displayGenres.push(...otherGenres.map(genre => ({ name: genre.name, disabled: false })));

    console.log('haaaaaa', this.displayGenres);
  }

  private getGenres(): { name: string; disabled: boolean }[] {
    return Object.values(Genre)
      .map(genre => ({
        name: genre
          .toLowerCase()
          .replace(/_/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase()),
        disabled: false,
      }))
      .sort((a, b) => a.name.localeCompare(b.name));
  }

  isCategory(item: string): boolean {
    return item === '--- Most Popular ---' || item === '--- Other Genres ---';
  }
}
