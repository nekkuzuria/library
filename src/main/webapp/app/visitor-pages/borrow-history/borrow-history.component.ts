import { Component, OnInit } from '@angular/core';
import { IPersonalStorage } from '../personal-storage/personal-storage.model';
import { PersonalStorageService } from '../personal-storage/personal-storage.service';
import dayjs from 'dayjs/esm';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'jhi-borrow-history',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './borrow-history.component.html',
  styleUrl: './borrow-history.component.scss',
})
export class BorrowHistoryComponent implements OnInit {
  visitorBookStorages?: IPersonalStorage[];

  constructor(private personalStorageService: PersonalStorageService) {}

  ngOnInit(): void {
    this.loadCurrentUserBookStorage();
  }

  loadCurrentUserBookStorage(): void {
    this.personalStorageService.query().subscribe(response => {
      this.visitorBookStorages = response.body || [];
      this.visitorBookStorages = this.visitorBookStorages.filter(vbs => vbs.returnDate !== null);
    });
  }

  convertDate(date: dayjs.Dayjs | null | undefined) {
    return date ? dayjs(date).format('DD/MM/YYYY') : null;
  }

  trackId(index: number, item: IPersonalStorage): number {
    return item.id;
  }
}
