import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { LibraryService } from '../service/library.service';
import { ILibrary } from '../library.model';
import { LibraryFormService } from './library-form.service';

import { LibraryUpdateComponent } from './library-update.component';

describe('Library Management Update Component', () => {
  let comp: LibraryUpdateComponent;
  let fixture: ComponentFixture<LibraryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let libraryFormService: LibraryFormService;
  let libraryService: LibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LibraryUpdateComponent],
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
      .overrideTemplate(LibraryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LibraryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    libraryFormService = TestBed.inject(LibraryFormService);
    libraryService = TestBed.inject(LibraryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const library: ILibrary = { id: 456 };

      activatedRoute.data = of({ library });
      comp.ngOnInit();

      expect(comp.library).toEqual(library);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrary>>();
      const library = { id: 123 };
      jest.spyOn(libraryFormService, 'getLibrary').mockReturnValue(library);
      jest.spyOn(libraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: library }));
      saveSubject.complete();

      // THEN
      expect(libraryFormService.getLibrary).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(libraryService.update).toHaveBeenCalledWith(expect.objectContaining(library));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrary>>();
      const library = { id: 123 };
      jest.spyOn(libraryFormService, 'getLibrary').mockReturnValue({ id: null });
      jest.spyOn(libraryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: library }));
      saveSubject.complete();

      // THEN
      expect(libraryFormService.getLibrary).toHaveBeenCalled();
      expect(libraryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILibrary>>();
      const library = { id: 123 };
      jest.spyOn(libraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(libraryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
