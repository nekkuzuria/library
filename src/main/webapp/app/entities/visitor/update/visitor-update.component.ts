import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { VisitorService } from '../service/visitor.service';
import { IVisitor } from '../visitor.model';
import { VisitorFormService, VisitorFormGroup } from './visitor-form.service';

@Component({
  standalone: true,
  selector: 'jhi-visitor-update',
  templateUrl: './visitor-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VisitorUpdateComponent implements OnInit {
  isSaving = false;
  visitor: IVisitor | null = null;

  addressesCollection: ILocation[] = [];
  librariesSharedCollection: ILibrary[] = [];
  usersSharedCollection: IUser[] = [];

  protected visitorService = inject(VisitorService);
  protected visitorFormService = inject(VisitorFormService);
  protected locationService = inject(LocationService);
  protected libraryService = inject(LibraryService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VisitorFormGroup = this.visitorFormService.createVisitorFormGroup();

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  compareLibrary = (o1: ILibrary | null, o2: ILibrary | null): boolean => this.libraryService.compareLibrary(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitor }) => {
      this.visitor = visitor;
      if (visitor) {
        this.updateForm(visitor);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const visitor = this.visitorFormService.getVisitor(this.editForm);
    if (visitor.id !== null) {
      this.subscribeToSaveResponse(this.visitorService.update(visitor));
    } else {
      this.subscribeToSaveResponse(this.visitorService.create(visitor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisitor>>): void {
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

  protected updateForm(visitor: IVisitor): void {
    this.visitor = visitor;
    this.visitorFormService.resetForm(this.editForm, visitor);

    this.addressesCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(this.addressesCollection, visitor.address);
    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(
      this.librariesSharedCollection,
      visitor.library,
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, visitor.user);
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query({ filter: 'visitor-is-null' })
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) => this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.visitor?.address)),
      )
      .subscribe((locations: ILocation[]) => (this.addressesCollection = locations));

    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(libraries, this.visitor?.library)))
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.visitor?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
