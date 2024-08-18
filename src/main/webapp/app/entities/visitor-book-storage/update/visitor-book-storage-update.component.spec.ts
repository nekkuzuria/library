import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IVisitor } from 'app/entities/visitor/visitor.model';
import { VisitorService } from 'app/entities/visitor/service/visitor.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IVisitorBookStorage } from '../visitor-book-storage.model';
import { VisitorBookStorageService } from '../service/visitor-book-storage.service';
import { VisitorBookStorageFormService } from './visitor-book-storage-form.service';

import { VisitorBookStorageUpdateComponent } from './visitor-book-storage-update.component';

describe('VisitorBookStorage Management Update Component', () => {
  let comp: VisitorBookStorageUpdateComponent;
  let fixture: ComponentFixture<VisitorBookStorageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitorBookStorageFormService: VisitorBookStorageFormService;
  let visitorBookStorageService: VisitorBookStorageService;
  let visitorService: VisitorService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VisitorBookStorageUpdateComponent],
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
      .overrideTemplate(VisitorBookStorageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitorBookStorageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitorBookStorageFormService = TestBed.inject(VisitorBookStorageFormService);
    visitorBookStorageService = TestBed.inject(VisitorBookStorageService);
    visitorService = TestBed.inject(VisitorService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Visitor query and add missing value', () => {
      const visitorBookStorage: IVisitorBookStorage = { id: 456 };
      const visitor: IVisitor = { id: 15088 };
      visitorBookStorage.visitor = visitor;

      const visitorCollection: IVisitor[] = [{ id: 14432 }];
      jest.spyOn(visitorService, 'query').mockReturnValue(of(new HttpResponse({ body: visitorCollection })));
      const additionalVisitors = [visitor];
      const expectedCollection: IVisitor[] = [...additionalVisitors, ...visitorCollection];
      jest.spyOn(visitorService, 'addVisitorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitorBookStorage });
      comp.ngOnInit();

      expect(visitorService.query).toHaveBeenCalled();
      expect(visitorService.addVisitorToCollectionIfMissing).toHaveBeenCalledWith(
        visitorCollection,
        ...additionalVisitors.map(expect.objectContaining),
      );
      expect(comp.visitorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Book query and add missing value', () => {
      const visitorBookStorage: IVisitorBookStorage = { id: 456 };
      const book: IBook = { id: 31716 };
      visitorBookStorage.book = book;

      const bookCollection: IBook[] = [{ id: 18544 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitorBookStorage });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining),
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const visitorBookStorage: IVisitorBookStorage = { id: 456 };
      const visitor: IVisitor = { id: 18139 };
      visitorBookStorage.visitor = visitor;
      const book: IBook = { id: 21259 };
      visitorBookStorage.book = book;

      activatedRoute.data = of({ visitorBookStorage });
      comp.ngOnInit();

      expect(comp.visitorsSharedCollection).toContain(visitor);
      expect(comp.booksSharedCollection).toContain(book);
      expect(comp.visitorBookStorage).toEqual(visitorBookStorage);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitorBookStorage>>();
      const visitorBookStorage = { id: 123 };
      jest.spyOn(visitorBookStorageFormService, 'getVisitorBookStorage').mockReturnValue(visitorBookStorage);
      jest.spyOn(visitorBookStorageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitorBookStorage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitorBookStorage }));
      saveSubject.complete();

      // THEN
      expect(visitorBookStorageFormService.getVisitorBookStorage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitorBookStorageService.update).toHaveBeenCalledWith(expect.objectContaining(visitorBookStorage));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitorBookStorage>>();
      const visitorBookStorage = { id: 123 };
      jest.spyOn(visitorBookStorageFormService, 'getVisitorBookStorage').mockReturnValue({ id: null });
      jest.spyOn(visitorBookStorageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitorBookStorage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitorBookStorage }));
      saveSubject.complete();

      // THEN
      expect(visitorBookStorageFormService.getVisitorBookStorage).toHaveBeenCalled();
      expect(visitorBookStorageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitorBookStorage>>();
      const visitorBookStorage = { id: 123 };
      jest.spyOn(visitorBookStorageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitorBookStorage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitorBookStorageService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVisitor', () => {
      it('Should forward to visitorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(visitorService, 'compareVisitor');
        comp.compareVisitor(entity, entity2);
        expect(visitorService.compareVisitor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBook', () => {
      it('Should forward to bookService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
