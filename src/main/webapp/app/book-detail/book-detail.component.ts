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
          this.loadBookStorage(this.book.id);
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
        this.visitorService.getVisitorId().subscribe(visitorId => {
          console.log(visitorId);
          const newRecord: NewVisitorBookStorage = {
            id: null,
            visitor: { id: visitorId },
            book: { id: this.book!.id },
            borrowDate: dayjs().startOf('day'),
            returnDate: null,
            quantity: this.selectedQuantity,
          };

          this.visitorBookStorageService.create(newRecord).subscribe(() => {
            if (this.book && this.book.id) {
              this.updateBookQuantity(this.book.id, -this.selectedQuantity);
              alert('Book borrowed successfully');
            } else {
              alert('Book borrow failed');
            }
          });
        });
      } else {
        alert('Could not determine user ID');
      }
    } else {
      alert('Selected quantity exceeds available stock.');
    }
  }

  updateBookQuantity(bookId: number, quantityChange: number): void {
    this.bookStorageService.find(bookId).subscribe({
      next: (response: HttpResponse<IBookStorage>) => {
        const storageRecord = response.body;
        if (storageRecord) {
          if (storageRecord.quantity == null) {
            storageRecord.quantity = 0;
          }
          storageRecord.quantity += quantityChange;
          this.bookStorageService.update(storageRecord).subscribe(response => {
            const bookStorage = response.body ?? undefined;
            if (bookStorage) {
              this.quantity = bookStorage.quantity!;
            }
          });
        }
      },
      error: err => {
        console.error('Error updating quantity:', err);
      },
    });
  }
}
