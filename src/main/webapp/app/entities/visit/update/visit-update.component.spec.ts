import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { ILibrarian } from 'app/entities/librarian/librarian.model';
import { LibrarianService } from 'app/entities/librarian/service/librarian.service';
import { IVisitor } from 'app/entities/visitor/visitor.model';
import { VisitorService } from 'app/entities/visitor/service/visitor.service';
import { IVisit } from '../visit.model';
import { VisitService } from '../service/visit.service';
import { VisitFormService } from './visit-form.service';

import { VisitUpdateComponent } from './visit-update.component';

describe('Visit Management Update Component', () => {
  let comp: VisitUpdateComponent;
  let fixture: ComponentFixture<VisitUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitFormService: VisitFormService;
  let visitService: VisitService;
  let libraryService: LibraryService;
  let librarianService: LibrarianService;
  let visitorService: VisitorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VisitUpdateComponent],
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
      .overrideTemplate(VisitUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitFormService = TestBed.inject(VisitFormService);
    visitService = TestBed.inject(VisitService);
    libraryService = TestBed.inject(LibraryService);
    librarianService = TestBed.inject(LibrarianService);
    visitorService = TestBed.inject(VisitorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Library query and add missing value', () => {
      const visit: IVisit = { id: 456 };
      const library: ILibrary = { id: 12776 };
      visit.library = library;

      const libraryCollection: ILibrary[] = [{ id: 21278 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(
        libraryCollection,
        ...additionalLibraries.map(expect.objectContaining),
      );
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Librarian query and add missing value', () => {
      const visit: IVisit = { id: 456 };
      const librarian: ILibrarian = { id: 17775 };
      visit.librarian = librarian;

      const librarianCollection: ILibrarian[] = [{ id: 25415 }];
      jest.spyOn(librarianService, 'query').mockReturnValue(of(new HttpResponse({ body: librarianCollection })));
      const additionalLibrarians = [librarian];
      const expectedCollection: ILibrarian[] = [...additionalLibrarians, ...librarianCollection];
      jest.spyOn(librarianService, 'addLibrarianToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      expect(librarianService.query).toHaveBeenCalled();
      expect(librarianService.addLibrarianToCollectionIfMissing).toHaveBeenCalledWith(
        librarianCollection,
        ...additionalLibrarians.map(expect.objectContaining),
      );
      expect(comp.librariansSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Visitor query and add missing value', () => {
      const visit: IVisit = { id: 456 };
      const visitor: IVisitor = { id: 514 };
      visit.visitor = visitor;

      const visitorCollection: IVisitor[] = [{ id: 32626 }];
      jest.spyOn(visitorService, 'query').mockReturnValue(of(new HttpResponse({ body: visitorCollection })));
      const additionalVisitors = [visitor];
      const expectedCollection: IVisitor[] = [...additionalVisitors, ...visitorCollection];
      jest.spyOn(visitorService, 'addVisitorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      expect(visitorService.query).toHaveBeenCalled();
      expect(visitorService.addVisitorToCollectionIfMissing).toHaveBeenCalledWith(
        visitorCollection,
        ...additionalVisitors.map(expect.objectContaining),
      );
      expect(comp.visitorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const visit: IVisit = { id: 456 };
      const library: ILibrary = { id: 15813 };
      visit.library = library;
      const librarian: ILibrarian = { id: 27169 };
      visit.librarian = librarian;
      const visitor: IVisitor = { id: 11409 };
      visit.visitor = visitor;

      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      expect(comp.librariesSharedCollection).toContain(library);
      expect(comp.librariansSharedCollection).toContain(librarian);
      expect(comp.visitorsSharedCollection).toContain(visitor);
      expect(comp.visit).toEqual(visit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisit>>();
      const visit = { id: 123 };
      jest.spyOn(visitFormService, 'getVisit').mockReturnValue(visit);
      jest.spyOn(visitService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visit }));
      saveSubject.complete();

      // THEN
      expect(visitFormService.getVisit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitService.update).toHaveBeenCalledWith(expect.objectContaining(visit));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisit>>();
      const visit = { id: 123 };
      jest.spyOn(visitFormService, 'getVisit').mockReturnValue({ id: null });
      jest.spyOn(visitService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visit }));
      saveSubject.complete();

      // THEN
      expect(visitFormService.getVisit).toHaveBeenCalled();
      expect(visitService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVisit>>();
      const visit = { id: 123 };
      jest.spyOn(visitService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitService.update).toHaveBeenCalled();
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

    describe('compareLibrarian', () => {
      it('Should forward to librarianService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(librarianService, 'compareLibrarian');
        comp.compareLibrarian(entity, entity2);
        expect(librarianService.compareLibrarian).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareVisitor', () => {
      it('Should forward to visitorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(visitorService, 'compareVisitor');
        comp.compareVisitor(entity, entity2);
        expect(visitorService.compareVisitor).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
