import { Component, AfterViewInit, ElementRef, inject, signal, viewChild, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PublicService } from 'app/core/public-service/public.service';
import { IPublicLibrary } from 'app/core/public-service/public.library.model';
import { NgSelectModule } from '@ng-select/ng-select';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';
import SharedModule from 'app/shared/shared.module';
import PasswordStrengthBarComponent from '../password/password-strength-bar/password-strength-bar.component';
import { RegisterService } from './register.service';

@Component({
  standalone: true,
  selector: 'jhi-register',
  imports: [SharedModule, RouterModule, FormsModule, ReactiveFormsModule, PasswordStrengthBarComponent, NgSelectModule],
  templateUrl: './register.component.html',
})
export default class RegisterComponent implements OnInit, AfterViewInit {
  // login = viewChild.required<ElementRef>('login');
  libraries: IPublicLibrary[] = [];
  roles: string[] = [];

  doNotMatch = signal(false);
  error = signal(false);
  errorEmailExists = signal(false);
  errorUserExists = signal(false);
  success = signal(false);

  registerForm = new FormGroup({
    roleChoice: new FormControl(null, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    libraryId: new FormControl(null, {
      nonNullable: true,
      validators: [Validators.required],
    }),
    firstName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    lastName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    dateOfBirth: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    phoneNumber: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(13), Validators.pattern('^\\+?[1-9][0-9]{4,13}$')],
    }),
    address: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    streetAddress: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100), Validators.pattern('^[a-zA-Z0-9 ,.-/]+$')],
    }),
    postalCode: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern('^[0-9]{5}(-[0-9]{4})?$')],
    }),
    city: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z ]+$')],
    }),
    stateProvince: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z ]+$')],
    }),
    login: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  constructor(private publicService: PublicService) {}
  private registerService = inject(RegisterService);
  ngOnInit(): void {
    this.loadLibraries();
    this.roles = ['Visitor', 'Librarian'];
  }
  ngAfterViewInit(): void {
    // this.login().nativeElement.focus();
  }
  loadLibraries(): void {
    this.publicService.query().subscribe({
      next: response => {
        this.libraries = response.body || [];
      },
      error: error => {
        console.error('Error fetching libraries:', error);
      },
      complete: () => {
        console.log('Library fetch complete');
      },
    });
  }
  register(): void {
    this.doNotMatch.set(false);
    this.error.set(false);
    this.errorEmailExists.set(false);
    this.errorUserExists.set(false);

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch.set(true);
    } else {
      const {
        roleChoice,
        libraryId,
        firstName,
        lastName,
        dateOfBirth,
        phoneNumber,
        streetAddress,
        postalCode,
        city,
        stateProvince,
        login,
        email,
      } = this.registerForm.getRawValue();

      this.registerService
        .save({
          roleChoice,
          libraryId: Number(libraryId),
          firstName,
          lastName,
          dateOfBirth,
          phoneNumber,
          streetAddress,
          postalCode,
          city,
          stateProvince,
          login,
          email,
          password,
          langKey: 'en',
        })
        .subscribe({ next: () => this.success.set(true), error: response => this.processError(response) });
    }
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists.set(true);
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists.set(true);
    } else {
      this.error.set(true);
    }
  }

  restrictNonNumeric(event: KeyboardEvent): void {
    if (event.key === 'e' || event.key === 'E' || event.key === '-' || event.key === '+' || event.key === '.') {
      event.preventDefault();
    }
  }
}
