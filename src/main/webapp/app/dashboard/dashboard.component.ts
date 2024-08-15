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

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [NgSelectComponent, NgOptionTemplateDirective, NgLabelTemplateDirective, CommonModule, FormsModule],
})
export class DashboardComponent implements OnInit {
  books?: IBook[];
  genres: string[] = [];
  filteredBooks: IBook[] = [];
  searchQuery: string = '';
  selectedGenre: string | null = null;

  totalItems = 0;
  isLoading = false;
  itemsPerPage = ITEMS_PER_PAGE;
  sortState = sortStateSignal({});
  page = 1;
  sortOption: string = '';

  protected sortService = inject(SortService);

  sortOptions = [
    { value: 'title', label: 'Sort by Title' },
    { value: 'author', label: 'Sort by Author' },
  ];

  constructor(private bookService: BookService) {}

  trackId = (_index: number, item: IBook): number => this.bookService.getBookIdentifier(item);
  ngOnInit(): void {
    this.load();
    this.genres = Object.values(Genre);
    console.log(this.genres);
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

      const matchesGenre = this.selectedGenre ? book.genre === this.selectedGenre : true;

      return matchesSearchQuery && matchesGenre;
    });

    this.sortBooks();
  }

  sortBooks(): void {
    const sortValue = typeof this.sortOption === 'string' ? this.sortOption : (this.sortOption as { value: string }).value;
    console.log(sortValue);
    if (sortValue === 'title') {
      this.filteredBooks.sort((a, b) => {
        const titleA = a.title ?? '';
        const titleB = b.title ?? '';
        return titleA.localeCompare(titleB);
      });
    } else if (sortValue === 'author') {
      this.filteredBooks.sort((a, b) => {
        const authorA = a.author ?? '';
        const authorB = b.author ?? '';
        return authorA.localeCompare(authorB);
      });
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.books = dataFromBody;
    this.filteredBooks = this.books;
    this.sortBooks();
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
}
