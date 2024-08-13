import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { BookStorageService } from '../service/book-storage.service';
import { IBookStorage } from '../book-storage.model';
import { BookStorageFormService } from './book-storage-form.service';

import { BookStorageUpdateComponent } from './book-storage-update.component';

describe('BookStorage Management Update Component', () => {
  let comp: BookStorageUpdateComponent;
  let fixture: ComponentFixture<BookStorageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookStorageFormService: BookStorageFormService;
  let bookStorageService: BookStorageService;
  let libraryService: LibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookStorageUpdateComponent],
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
      .overrideTemplate(BookStorageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookStorageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookStorageFormService = TestBed.inject(BookStorageFormService);
    bookStorageService = TestBed.inject(BookStorageService);
    libraryService = TestBed.inject(LibraryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Library query and add missing value', () => {
      const bookStorage: IBookStorage = { id: 456 };
      const library: ILibrary = { id: 20308 };
      bookStorage.library = library;

      const libraryCollection: ILibrary[] = [{ id: 11643 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookStorage });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(
        libraryCollection,
        ...additionalLibraries.map(expect.objectContaining),
      );
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bookStorage: IBookStorage = { id: 456 };
      const library: ILibrary = { id: 14183 };
      bookStorage.library = library;

      activatedRoute.data = of({ bookStorage });
      comp.ngOnInit();

      expect(comp.librariesSharedCollection).toContain(library);
      expect(comp.bookStorage).toEqual(bookStorage);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookStorage>>();
      const bookStorage = { id: 123 };
      jest.spyOn(bookStorageFormService, 'getBookStorage').mockReturnValue(bookStorage);
      jest.spyOn(bookStorageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookStorage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookStorage }));
      saveSubject.complete();

      // THEN
      expect(bookStorageFormService.getBookStorage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookStorageService.update).toHaveBeenCalledWith(expect.objectContaining(bookStorage));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookStorage>>();
      const bookStorage = { id: 123 };
      jest.spyOn(bookStorageFormService, 'getBookStorage').mockReturnValue({ id: null });
      jest.spyOn(bookStorageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookStorage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookStorage }));
      saveSubject.complete();

      // THEN
      expect(bookStorageFormService.getBookStorage).toHaveBeenCalled();
      expect(bookStorageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookStorage>>();
      const bookStorage = { id: 123 };
      jest.spyOn(bookStorageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookStorage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookStorageService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLibrary', () => {
      it('Should forward to libraryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(libraryService, 'compareLibrary');
        comp.compareLibrary(entity, entity2);
        expect(libraryService.compareLibrary).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
