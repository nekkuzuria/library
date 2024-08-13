import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { ILibrarian } from 'app/entities/librarian/librarian.model';
import { LibrarianService } from 'app/entities/librarian/service/librarian.service';
import { IVisitor } from 'app/entities/visitor/visitor.model';
import { VisitorService } from 'app/entities/visitor/service/visitor.service';
import { VisitService } from '../service/visit.service';
import { IVisit } from '../visit.model';
import { VisitFormService, VisitFormGroup } from './visit-form.service';

@Component({
  standalone: true,
  selector: 'jhi-visit-update',
  templateUrl: './visit-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VisitUpdateComponent implements OnInit {
  isSaving = false;
  visit: IVisit | null = null;

  librariesSharedCollection: ILibrary[] = [];
  librariansSharedCollection: ILibrarian[] = [];
  visitorsSharedCollection: IVisitor[] = [];

  protected visitService = inject(VisitService);
  protected visitFormService = inject(VisitFormService);
  protected libraryService = inject(LibraryService);
  protected librarianService = inject(LibrarianService);
  protected visitorService = inject(VisitorService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VisitFormGroup = this.visitFormService.createVisitFormGroup();

  compareLibrary = (o1: ILibrary | null, o2: ILibrary | null): boolean => this.libraryService.compareLibrary(o1, o2);

  compareLibrarian = (o1: ILibrarian | null, o2: ILibrarian | null): boolean => this.librarianService.compareLibrarian(o1, o2);

  compareVisitor = (o1: IVisitor | null, o2: IVisitor | null): boolean => this.visitorService.compareVisitor(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visit }) => {
      this.visit = visit;
      if (visit) {
        this.updateForm(visit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const visit = this.visitFormService.getVisit(this.editForm);
    if (visit.id !== null) {
      this.subscribeToSaveResponse(this.visitService.update(visit));
    } else {
      this.subscribeToSaveResponse(this.visitService.create(visit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisit>>): void {
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

  protected updateForm(visit: IVisit): void {
    this.visit = visit;
    this.visitFormService.resetForm(this.editForm, visit);

    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(
      this.librariesSharedCollection,
      visit.library,
    );
    this.librariansSharedCollection = this.librarianService.addLibrarianToCollectionIfMissing<ILibrarian>(
      this.librariansSharedCollection,
      visit.librarian,
    );
    this.visitorsSharedCollection = this.visitorService.addVisitorToCollectionIfMissing<IVisitor>(
      this.visitorsSharedCollection,
      visit.visitor,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing<ILibrary>(libraries, this.visit?.library)))
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));

    this.librarianService
      .query()
      .pipe(map((res: HttpResponse<ILibrarian[]>) => res.body ?? []))
      .pipe(
        map((librarians: ILibrarian[]) =>
          this.librarianService.addLibrarianToCollectionIfMissing<ILibrarian>(librarians, this.visit?.librarian),
        ),
      )
      .subscribe((librarians: ILibrarian[]) => (this.librariansSharedCollection = librarians));

    this.visitorService
      .query()
      .pipe(map((res: HttpResponse<IVisitor[]>) => res.body ?? []))
      .pipe(map((visitors: IVisitor[]) => this.visitorService.addVisitorToCollectionIfMissing<IVisitor>(visitors, this.visit?.visitor)))
      .subscribe((visitors: IVisitor[]) => (this.visitorsSharedCollection = visitors));
  }
}
