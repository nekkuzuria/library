import { Component, OnInit, AfterViewInit, ElementRef, inject, signal, viewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PublicService } from 'app/core/public-service/public.service';
import { IPublicLibrary } from 'app/core/public-service/public.library.model';
import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { NgSelectModule } from '@ng-select/ng-select';
import { Login } from './login.model';

@Component({
  standalone: true,
  selector: 'jhi-login',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule, NgSelectModule],
  templateUrl: './login.component.html',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  username = viewChild.required<ElementRef>('username');
  libraries: IPublicLibrary[] = [];
  authenticationError = signal(false);
  unauthorizedError = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
    libraryId: new FormControl(null, { nonNullable: true, validators: [Validators.required] }),
  });

  private accountService = inject(AccountService);
  private loginService = inject(LoginService);
  private router = inject(Router);

  constructor(private publicService: PublicService) {}
  ngOnInit(): void {
    this.loadLibraries();
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['/dashboard']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

  login(): void {
    const credentials: Login = {
      username: this.loginForm.get('username')?.value || '',
      password: this.loginForm.get('password')?.value || '',
      rememberMe: this.loginForm.get('rememberMe')?.value || false,
      libraryId: this.loginForm.get('libraryId')?.value || 0,
    };
    this.loginService.login(credentials).subscribe({
      next: () => {
        this.authenticationError.set(false);
        this.unauthorizedError.set(false);
        if (!this.router.getCurrentNavigation()) {
          // There were no routing during login (eg from navigationToStoredUrl)
          this.router.navigate(['/dashboard']);
        }
      },
      error: err => {
        if (err.status === 403) {
          this.unauthorizedError.set(true);
        } else {
          this.authenticationError.set(true);
        }
      },
    });
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
        console.log('Library fetch complete', this.libraries);
      },
    });
  }
}
