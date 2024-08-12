import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const bookStorage: IBookStorage = { id: 456 };

      activatedRoute.data = of({ bookStorage });
      comp.ngOnInit();

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
});
