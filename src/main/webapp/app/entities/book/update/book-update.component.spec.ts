import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IBookStorage } from 'app/entities/book-storage/book-storage.model';
import { BookStorageService } from 'app/entities/book-storage/service/book-storage.service';
import { BookService } from '../service/book.service';
import { IBook } from '../book.model';
import { BookFormService } from './book-form.service';

import { BookUpdateComponent } from './book-update.component';

describe('Book Management Update Component', () => {
  let comp: BookUpdateComponent;
  let fixture: ComponentFixture<BookUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookFormService: BookFormService;
  let bookService: BookService;
  let bookStorageService: BookStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookFormService = TestBed.inject(BookFormService);
    bookService = TestBed.inject(BookService);
    bookStorageService = TestBed.inject(BookStorageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BookStorage query and add missing value', () => {
      const book: IBook = { id: 456 };
      const bookStorage: IBookStorage = { id: 1225 };
      book.bookStorage = bookStorage;

      const bookStorageCollection: IBookStorage[] = [{ id: 10017 }];
      jest.spyOn(bookStorageService, 'query').mockReturnValue(of(new HttpResponse({ body: bookStorageCollection })));
      const additionalBookStorages = [bookStorage];
      const expectedCollection: IBookStorage[] = [...additionalBookStorages, ...bookStorageCollection];
      jest.spyOn(bookStorageService, 'addBookStorageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(bookStorageService.query).toHaveBeenCalled();
      expect(bookStorageService.addBookStorageToCollectionIfMissing).toHaveBeenCalledWith(
        bookStorageCollection,
        ...additionalBookStorages.map(expect.objectContaining),
      );
      expect(comp.bookStoragesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const book: IBook = { id: 456 };
      const bookStorage: IBookStorage = { id: 15932 };
      book.bookStorage = bookStorage;

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(comp.bookStoragesSharedCollection).toContain(bookStorage);
      expect(comp.book).toEqual(book);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue(book);
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookService.update).toHaveBeenCalledWith(expect.objectContaining(book));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue({ id: null });
      jest.spyOn(bookService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(bookService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBookStorage', () => {
      it('Should forward to bookStorageService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookStorageService, 'compareBookStorage');
        comp.compareBookStorage(entity, entity2);
        expect(bookStorageService.compareBookStorage).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
