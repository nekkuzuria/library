import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { VisitorBookStorageDetailComponent } from './visitor-book-storage-detail.component';

describe('VisitorBookStorage Management Detail Component', () => {
  let comp: VisitorBookStorageDetailComponent;
  let fixture: ComponentFixture<VisitorBookStorageDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisitorBookStorageDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: VisitorBookStorageDetailComponent,
              resolve: { visitorBookStorage: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(VisitorBookStorageDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitorBookStorageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load visitorBookStorage on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', VisitorBookStorageDetailComponent);

      // THEN
      expect(instance.visitorBookStorage()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
