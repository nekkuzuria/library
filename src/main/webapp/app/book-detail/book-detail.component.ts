import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { BookService } from '../entities/book/service/book.service';
import { BookStorageService } from '../entities/book-storage/service/book-storage.service';
import { VisitorBookStorageService } from '../entities/visitor-book-storage/service/visitor-book-storage.service';
import { IBook } from '../entities/book/book.model';
import { IBookStorage } from '../entities/book-storage/book-storage.model';
import { NewVisitorBookStorage } from '../entities/visitor-book-storage/visitor-book-storage.model';
import { HttpResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VisitorService } from '../entities/visitor/service/visitor.service';
import dayjs from 'dayjs/esm';
import { PendingTaskService } from 'app/pending-task/pending-task.service';
import { IPendingTaskVM, NewPendingTask } from 'app/pending-task/pending-task.model';

@Component({
  selector: 'app-book-detail',
  templateUrl: './book-detail.component.html',
  styleUrls: ['./book-detail.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class BookDetailComponent implements OnInit {
  book?: IBook;
  quantity: number = 0;
  selectedQuantity: number = 1;

  constructor(
    private activatedRoute: ActivatedRoute,
    private bookService: BookService,
    private bookStorageService: BookStorageService,
    private visitorBookStorageService: VisitorBookStorageService,
    private router: Router,
    private visitorService: VisitorService,
    private pendingTaskService: PendingTaskService,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((params: Params) => {
      const bookId = +params['id']; // Extract the book ID from the route parameters
      this.loadBookDetails(bookId);
    });
  }

  loadBookDetails(id: number): void {
    this.bookService.find(id).subscribe({
      next: (response: HttpResponse<IBook>) => {
        this.book = response.body ?? undefined;
        if (this.book) {
          console.log(this.book.bookStorageId);
          const storageId = this.book.bookStorageId;
          if (typeof storageId === 'number') {
            this.loadBookStorage(storageId); // Pass the numeric `id`
          } else {
            console.warn('Book storage ID is not available or not a number');
          }
        }
      },
      error: err => {
        console.error('Error fetching book details:', err);
      },
      complete: () => {
        console.log('Book details retrieval complete', this.book);
      },
    });
  }

  loadBookStorage(bookId: number): void {
    this.bookStorageService.find(bookId).subscribe({
      next: (response: HttpResponse<IBookStorage>) => {
        const storageRecord: IBookStorage | undefined = response.body ?? undefined;
        this.quantity = storageRecord?.quantity ?? 0;
        console.log('storagrerecord', storageRecord);
      },
      error: err => {
        console.error('Error fetching book storage:', err);
      },
      complete: () => {
        console.log('Book storage retrieval complete', this.quantity);
      },
    });
  }

  onBorrow(): void {
    if (this.selectedQuantity <= this.quantity && this.selectedQuantity != 0) {
      if (this.book) {
        const pendingTask: NewPendingTask = {
          id: null,
          bookId: this.book.id,
          type: 'BORROW',
          quantity: this.selectedQuantity,
        };
        this.pendingTaskService.create(pendingTask).subscribe({
          next: response => {
            console.log('Pending task created successfully:', response);
            alert('Borrow request created successfully');
          },
          error: error => {
            console.error('Error creating pending task:', error);
            alert('Borrow request created failed');
          },
        });
      } else {
        alert('Could not determine book ID');
      }
    } else {
      alert('Selected quantity exceeds available stock.');
    }
  }
}
