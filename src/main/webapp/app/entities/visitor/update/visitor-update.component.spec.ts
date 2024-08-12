import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { VisitorService } from '../service/visitor.service';
import { IVisitor } from '../visitor.model';
import { VisitorFormService } from './visitor-form.service';

import { VisitorUpdateComponent } from './visitor-update.component';

describe('Visitor Management Update Component', () => {
  let comp: VisitorUpdateComponent;
  let fixture: ComponentFixture<VisitorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitorFormService: VisitorFormService;
  let visitorService: VisitorService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const visitor: IVisitor = { id: 456 };

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

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
});
