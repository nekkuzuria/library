import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryVisitComponent } from './library-visit.component';

describe('LibraryVisitComponent', () => {
  let component: LibraryVisitComponent;
  let fixture: ComponentFixture<LibraryVisitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibraryVisitComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LibraryVisitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
