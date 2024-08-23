import { Component, inject, OnInit, signal } from '@angular/core';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';
import { SettingsService } from './settings.component.service';

@Component({
  standalone: true,
  selector: 'jhi-settings',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
})
export default class SettingsComponent implements OnInit {
  success = signal(false);
  imagePreview: string | ArrayBuffer | null = null;

  settingsForm: FormGroup | null = null;

  private settingsService = inject(SettingsService);

  constructor(private formBuilder: FormBuilder) {}
  ngOnInit(): void {
    this.settingsService.getUserSettings().subscribe(data => {
      if (data) {
        console.log(data.image);
        this.initializeForm(data);
        this.imagePreview = `data:image/jpeg;base64,${data.image}`;
      }
    });
  }

  private initializeForm(data: any): void {
    this.settingsForm = this.formBuilder.group({
      firstName: [data.firstName || '', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      lastName: [data.lastName || '', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      email: [data.email || '', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(254)]],
      login: [data.username || ''],
      phoneNumber: [data.phoneNumber || ''],
      dateOfBirth: [data.dateOfBirth || ''],
      streetAddress: [data.streetAddress || ''],
      postalCode: [data.postalCode || ''],
      city: [data.city || ''],
      stateProvince: [data.stateProvince || ''],
    });
  }

  save(): void {
    this.success.set(false);

    const account = this.settingsForm!.getRawValue();
    this.settingsService.updateUserSettings(account).subscribe(
      () => {
        this.success.set(true);
      },
      () => {
        this.success.set(false); // Reset in case of failure
      },
    );
  }

  onImageChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(file);

      this.settingsService.uploadImageFile(file).subscribe(
        () => {
          console.log('Image uploaded successfully');
        },
        error => {
          console.error('Error uploading image', error);
        },
      );

      this.settingsForm!.patchValue({ file });
    }
  }
}
