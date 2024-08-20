import { Component, OnInit } from '@angular/core';
import { LibraryVisitService } from './librrary-visit.service';
import { IVisitVM } from './library-visit.model';

@Component({
  selector: 'jhi-library-visit',
  standalone: true,
  imports: [],
  templateUrl: './library-visit.component.html',
  styleUrl: './library-visit.component.scss',
})
export class LibraryVisitComponent implements OnInit {
  visits?: IVisitVM[];

  constructor(private libraryVisitService: LibraryVisitService) {}

  ngOnInit(): void {
    this.loadCurrentLibraryVisit();
  }
  loadCurrentLibraryVisit(): void {
    this.libraryVisitService.query().subscribe(response => {
      this.visits = response.body || [];
    });
  }

  trackId(index: number, item: IVisitVM): number {
    return item.id;
  }
}
