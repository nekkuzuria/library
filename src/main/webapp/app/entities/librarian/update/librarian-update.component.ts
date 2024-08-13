import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { LibrarianService } from '../service/librarian.service';
import { ILibrarian } from '../librarian.model';
import { LibrarianFormService, LibrarianFormGroup } from './librarian-form.service';

@Component({
  standalone: true,
  selector: 'jhi-librarian-update',
  templateUrl: './librarian-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LibrarianUpdateComponent implements OnInit {
  isSaving = false;
  librarian: ILibrarian | null = null;

  librariesSharedCollection: ILibrary[] = [];
  locationsCollection: ILocation[] = [];

  protected librarianService = inject(LibrarianService);
  protected librarianFormService = inject(LibrarianFormService);
  protected libraryService = inject(LibraryService);
  protected locationService = inject(LocationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LibrarianFormGroup = this.librarianFormService.createLibrarianFormGroup();

  compareLibrary = (o1: ILibrary | null, o2: ILibrary | null): boolean => this.libraryService.compareLibrary(o1, o2);

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ librarian }) => {
      this.librarian = librarian;
      if (librarian) {
        this.updateForm(librarian);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const librarian = this.librarianFormService.getLibrarian(this.editForm);
    if (librarian.id !== null) {
      this.subscribeToSaveResponse(this.librarianService.update(librarian));
    } else {
      this.subscribeToSaveResponse(this.librarianService.create(librarian));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibrarian>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(librarian: ILibrarian): void {
    this.librarian = librarian;
    this.librarianFormService.resetForm(this.editForm, librarian);

    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(
      this.librariesSharedCollection,
      librarian.library,
    );
    this.locationsCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsCollection,
      librarian.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(
        map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(libraries, this.librarian?.library)),
      )
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));

    this.locationService
      .query({ filter: 'librarian-is-null' })
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.librarian?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsCollection = locations));
  }
}
