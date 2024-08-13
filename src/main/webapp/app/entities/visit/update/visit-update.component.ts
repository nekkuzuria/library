import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IVisit } from '../visit.model';
import { VisitService } from '../service/visit.service';
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

  protected visitService = inject(VisitService);
  protected visitFormService = inject(VisitFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VisitFormGroup = this.visitFormService.createVisitFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visit }) => {
      this.visit = visit;
      if (visit) {
        this.updateForm(visit);
      }
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
  }
}
