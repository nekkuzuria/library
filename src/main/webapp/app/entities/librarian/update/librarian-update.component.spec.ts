import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { ILibrarian } from '../librarian.model';
import { LibrarianService } from '../service/librarian.service';
import { LibrarianFormService } from './librarian-form.service';

import { LibrarianUpdateComponent } from './librarian-update.component';

describe('Librarian Management Update Component', () => {
  let comp: LibrarianUpdateComponent;
  let fixture: ComponentFixture<LibrarianUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let librarianFormService: LibrarianFormService;
  let librarianService: LibrarianService;
  let libraryService: LibraryService;
  let locationService: LocationService;

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
    libraryService = TestBed.inject(LibraryService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Library query and add missing value', () => {
      const librarian: ILibrarian = { id: 456 };
      const library: ILibrary = { id: 24333 };
      librarian.library = library;

      const libraryCollection: ILibrary[] = [{ id: 21817 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(
        libraryCollection,
        ...additionalLibraries.map(expect.objectContaining),
      );
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call location query and add missing value', () => {
      const librarian: ILibrarian = { id: 456 };
      const location: ILocation = { id: 3960 };
      librarian.location = location;

      const locationCollection: ILocation[] = [{ id: 5211 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const expectedCollection: ILocation[] = [location, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(locationCollection, location);
      expect(comp.locationsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const librarian: ILibrarian = { id: 456 };
      const library: ILibrary = { id: 5505 };
      librarian.library = library;
      const location: ILocation = { id: 17003 };
      librarian.location = location;

      activatedRoute.data = of({ librarian });
      comp.ngOnInit();

      expect(comp.librariesSharedCollection).toContain(library);
      expect(comp.locationsCollection).toContain(location);
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

    describe('compareLocation', () => {
      it('Should forward to locationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(locationService, 'compareLocation');
        comp.compareLocation(entity, entity2);
        expect(locationService.compareLocation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
