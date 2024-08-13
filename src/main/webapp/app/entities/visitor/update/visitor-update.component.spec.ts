import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { IVisitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';
import { VisitorFormService } from './visitor-form.service';

import { VisitorUpdateComponent } from './visitor-update.component';

describe('Visitor Management Update Component', () => {
  let comp: VisitorUpdateComponent;
  let fixture: ComponentFixture<VisitorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitorFormService: VisitorFormService;
  let visitorService: VisitorService;
  let locationService: LocationService;
  let libraryService: LibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VisitorUpdateComponent],
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
      .overrideTemplate(VisitorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitorFormService = TestBed.inject(VisitorFormService);
    visitorService = TestBed.inject(VisitorService);
    locationService = TestBed.inject(LocationService);
    libraryService = TestBed.inject(LibraryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call address query and add missing value', () => {
      const visitor: IVisitor = { id: 456 };
      const address: ILocation = { id: 15227 };
      visitor.address = address;

      const addressCollection: ILocation[] = [{ id: 12952 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
      const expectedCollection: ILocation[] = [address, ...addressCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(addressCollection, address);
      expect(comp.addressesCollection).toEqual(expectedCollection);
    });

    it('Should call Library query and add missing value', () => {
      const visitor: IVisitor = { id: 456 };
      const library: ILibrary = { id: 18654 };
      visitor.library = library;

      const libraryCollection: ILibrary[] = [{ id: 10277 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(
        libraryCollection,
        ...additionalLibraries.map(expect.objectContaining),
      );
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const visitor: IVisitor = { id: 456 };
      const address: ILocation = { id: 5737 };
      visitor.address = address;
      const library: ILibrary = { id: 1831 };
      visitor.library = library;

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      expect(comp.addressesCollection).toContain(address);
      expect(comp.librariesSharedCollection).toContain(library);
      expect(comp.visitor).toEqual(visitor);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitor>>();
      const visitor = { id: 123 };
      jest.spyOn(visitorFormService, 'getVisitor').mockReturnValue(visitor);
      jest.spyOn(visitorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitor }));
      saveSubject.complete();

      // THEN
      expect(visitorFormService.getVisitor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitorService.update).toHaveBeenCalledWith(expect.objectContaining(visitor));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitor>>();
      const visitor = { id: 123 };
      jest.spyOn(visitorFormService, 'getVisitor').mockReturnValue({ id: null });
      jest.spyOn(visitorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitor }));
      saveSubject.complete();

      // THEN
      expect(visitorFormService.getVisitor).toHaveBeenCalled();
      expect(visitorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisitor>>();
      const visitor = { id: 123 };
      jest.spyOn(visitorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLocation', () => {
      it('Should forward to locationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(locationService, 'compareLocation');
        comp.compareLocation(entity, entity2);
        expect(locationService.compareLocation).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
