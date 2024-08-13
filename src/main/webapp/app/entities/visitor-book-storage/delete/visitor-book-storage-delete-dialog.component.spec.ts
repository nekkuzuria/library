jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { VisitorBookStorageService } from '../service/visitor-book-storage.service';

import { VisitorBookStorageDeleteDialogComponent } from './visitor-book-storage-delete-dialog.component';

describe('VisitorBookStorage Management Delete Component', () => {
  let comp: VisitorBookStorageDeleteDialogComponent;
  let fixture: ComponentFixture<VisitorBookStorageDeleteDialogComponent>;
  let service: VisitorBookStorageService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VisitorBookStorageDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(VisitorBookStorageDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VisitorBookStorageDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VisitorBookStorageService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
