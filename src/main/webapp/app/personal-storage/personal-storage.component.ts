import { Component, OnInit } from '@angular/core';
import { IPersonalStorage } from './personal-storage.model';
import { PersonalStorageService } from './personal-storage.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'jhi-personal-storage',
  templateUrl: './personal-storage.component.html',
  standalone: true,
  imports: [RouterModule],
})
export class PersonalStorageComponent implements OnInit {
  visitorBookStorages?: IPersonalStorage[];

  constructor(private personalStorageService: PersonalStorageService) {}

  ngOnInit(): void {
    this.loadCurrentUserBookStorage();
  }

  loadCurrentUserBookStorage(): void {
    this.personalStorageService.query().subscribe(response => {
      this.visitorBookStorages = response.body || [];
    });
  }

  trackId(index: number, item: IPersonalStorage): number {
    return item.id;
  }
}
