import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { LibrarianService } from '../service/librarian.service';
import { ILibrarian } from '../librarian.model';
import { LibrarianFormService } from './librarian-form.service';

import { LibrarianUpdateComponent } from './librarian-update.component';

describe('Librarian Management Update Component', () => {
  let comp: LibrarianUpdateComponent;
  let fixture: ComponentFixture<LibrarianUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let librarianFormService: LibrarianFormService;
  let librarianService: LibrarianService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LibrarianUpdateComponent],
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
      .overrideTemplate(LibrarianUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LibrarianUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    librarianFormService = TestBed.inject(LibrarianFormService);
    librarianService = TestBed.inject(LibrarianService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const librarian: ILibrarian = { id: 456 };

      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      expect(comp.librarian).toEqual(librarian);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrarian>>();
      const librarian = { id: 123 };
      jest.spyOn(librarianFormService, 'getLibrarian').mockReturnValue(librarian);
      jest.spyOn(librarianService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: librarian }));
      saveSubject.complete();

      // THEN
      expect(librarianFormService.getLibrarian).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(librarianService.update).toHaveBeenCalledWith(expect.objectContaining(librarian));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrarian>>();
      const librarian = { id: 123 };
      jest.spyOn(librarianFormService, 'getLibrarian').mockReturnValue({ id: null });
      jest.spyOn(librarianService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ librarian: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: librarian }));
      saveSubject.complete();

      // THEN
      expect(librarianFormService.getLibrarian).toHaveBeenCalled();
      expect(librarianService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrarian>>();
      const librarian = { id: 123 };
      jest.spyOn(librarianService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(librarianService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
