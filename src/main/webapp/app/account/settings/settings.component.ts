import { Component, inject, OnInit, signal } from '@angular/core';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';
import { SettingsService } from './settings.component.service';
import { NgbDatepickerModule, NgbDate, NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';

declare var bootstrap: any;
@Component({
  standalone: true,
  selector: 'jhi-settings',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, NgbDatepickerModule],
  templateUrl: './settings.component.html',
})
export default class SettingsComponent implements OnInit {
  success = signal(false);
  imagePreview: string | ArrayBuffer | null = null;
  file = null;
  settingsForm: FormGroup | null = null;

  private settingsService = inject(SettingsService);

  constructor(
    private formBuilder: FormBuilder,
    private ngbDateParserFormatter: NgbDateParserFormatter,
  ) {}
  ngOnInit(): void {
    this.settingsService.getUserSettings().subscribe(data => {
      if (data) {
        this.initializeForm(data);
        this.imagePreview = `data:image/jpeg;base64,${data.image}`;
      }
    });
  }

  private initializeForm(data: any): void {
    this.settingsForm = this.formBuilder.group({
      firstName: [data.firstName || '', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      lastName: [data.lastName || '', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      email: [data.email || '', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(50)]],
      login: [data.username || ''],
      phoneNumber: [
        data.phoneNumber || '',
        [Validators.required, Validators.minLength(5), Validators.maxLength(13), Validators.pattern('^\\+?[1-9][0-9]{4,13}$')],
      ],
      streetAddress: [data.streetAddress || '', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      postalCode: [data.postalCode || '', [Validators.required, Validators.pattern('^[0-9]{5}(-[0-9]{4})?$')]],
      city: [data.city || '', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z ]+$')]],
      stateProvince: [
        data.stateProvince || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z ]+$')],
      ],
      dateOfBirth: [data.dateOfBirth, [Validators.required]],
    });
  }

  private convertDate(dateString: string): { year: number; month: number; day: number } | null {
    if (!dateString) return null;

    const date = new Date(dateString);
    return {
      year: date.getFullYear(),
      month: date.getMonth() + 1, // months are zero-based
      day: date.getDate(),
    };
  }

  save(): void {
    this.success.set(false);
    //     this.settingsForm!.dateOfBirth.setValue(this.convertDate(this.settingsForm!.get("dateOfBirth")));
    const account = this.settingsForm!.getRawValue();
    console.log(account);
    console.log(this.file);
    if (this.file) {
      this.settingsService.updateUserSettings(account, this.file).subscribe(
        () => {
          this.success.set(true);
        },
        () => {
          this.success.set(false);
        },
      );
    } else {
      this.settingsService.updateUserSettings(account).subscribe(
        () => {
          this.success.set(true);
        },
        () => {
          this.success.set(false);
        },
      );
    }
  }

  onImageChange(event: any): void {
    this.file = event.target.files[0];
    if (this.file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(this.file);

      this.settingsForm!.patchValue({ file: this.file });
    }
  }

  onDateSelect(date: any): void {
    const formattedDate = this.ngbDateParserFormatter.format(date);
    this.settingsForm!.get('dateOfBirth')?.setValue(formattedDate);
  }

  openImagePreview(): void {
    const modalElement = document.getElementById('imagePreviewModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
  }
}
