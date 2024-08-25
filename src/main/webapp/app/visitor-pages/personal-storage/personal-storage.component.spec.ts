import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalStorageComponent } from './personal-storage.component';

describe('PersonalStorageComponent', () => {
  let component: PersonalStorageComponent;
  let fixture: ComponentFixture<PersonalStorageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonalStorageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PersonalStorageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
