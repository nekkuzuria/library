import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

@Injectable({
  providedIn: 'root',
})
export class SettingsService {
  private resourceUrl = this.applicationConfigService.getEndpointFor('/api/files');
  private apiUrl = this.applicationConfigService.getEndpointFor('/api/account');
  private userSettings = this.applicationConfigService.getEndpointFor('/api/user-settings');

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  getUserSettings(): Observable<any> {
    console.log(this.http.get<any>(this.userSettings));
    return this.http.get<any>(this.userSettings);
  }

  updateUserSettings(settings: any, file?: File): Observable<any> {
    const formData = new FormData();
    formData.append('user', JSON.stringify(settings));
    if (file) {
      formData.append('file', file);
    }
    console.log(file);
    return this.http.post<any>(this.apiUrl, formData);
  }

  uploadImageFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.resourceUrl}/upload-user-image`, formData);
  }
}
