import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookStorageDetailComponent } from './book-storage-detail.component';

describe('BookStorage Management Detail Component', () => {
  let comp: BookStorageDetailComponent;
  let fixture: ComponentFixture<BookStorageDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookStorageDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: BookStorageDetailComponent,
              resolve: { bookStorage: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookStorageDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookStorageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookStorage on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookStorageDetailComponent);

      // THEN
      expect(instance.bookStorage()).toEqual(expect.objectContaining({ id: 123 }));
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
