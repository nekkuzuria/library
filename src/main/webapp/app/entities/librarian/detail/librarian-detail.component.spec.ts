import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { LibrarianDetailComponent } from './librarian-detail.component';

describe('Librarian Management Detail Component', () => {
  let comp: LibrarianDetailComponent;
  let fixture: ComponentFixture<LibrarianDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibrarianDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: LibrarianDetailComponent,
              resolve: { librarian: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LibrarianDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LibrarianDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load librarian on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LibrarianDetailComponent);

      // THEN
      expect(instance.librarian()).toEqual(expect.objectContaining({ id: 123 }));
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
