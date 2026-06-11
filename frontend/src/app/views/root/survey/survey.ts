import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { Router } from '@angular/router';
import { SurveyService } from "../../../services/survey.service";
import { Survey } from "../../../models/survey";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog } from '../../../components/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-survey-manager',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatCardModule, MatSlideToggleModule, MatTooltipModule, MatChipsModule
  ],
  templateUrl: './survey.html',
  styleUrl: './survey.scss'
})
export class SurveyManagerComponent implements OnInit {
  private router = inject(Router);
  private surveyService = inject(SurveyService);
  private toastr = inject(ToastrService);
  private dialog = inject(MatDialog);

  dataSource = new MatTableDataSource<Survey>([]);
  displayedColumns: string[] = ['status', 'title', 'questions', 'date', 'actions'];

  ngOnInit() {
    this.loadSurveys();
  }

  loadSurveys() {
    this.surveyService.getAllSurveys().subscribe({
      next: (data) => {
        this.dataSource.data = data;
      },
      error: () => this.toastr.error('Error al conectar con el servidor', 'Error')
    });
  }

  // ========== ACTIVAR/DESACTIVAR ==========
  toggleActive(survey: Survey) {
    if (survey.isActive) {
      this.toastr.info('Este cuestionario ya está activo', 'Información');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '400px',
      data: {
        message: '¿Desea activar este cuestionario? El cuestionario activo actual será desactivado automáticamente.'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.surveyService.activateSurvey(survey.id).subscribe({
          next: () => {
            this.toastr.success('Cuestionario activado correctamente', 'Éxito');
            this.loadSurveys();
          },
          error: (err) => {
            this.toastr.error('Error al activar el cuestionario', 'Error');
            console.error(err);
          }
        });
      }
    });
  }

  // ========== ELIMINAR ==========
  deleteSurvey(survey: Survey) {
    if (survey.isActive) {
      this.toastr.warning('No se puede eliminar el cuestionario activo', 'Advertencia');
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '450px',
      data: {
        message: `¿Estás seguro de que deseas eliminar el cuestionario "${survey.title}"?\n\nNota: No se podrá eliminar si ya tiene respuestas de usuarios registradas.`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.surveyService.deleteSurvey(survey.id).subscribe({
          next: () => {
            this.loadSurveys();
            this.toastr.success('El cuestionario ha sido eliminado correctamente', 'Eliminado');
          },
          error: (err) => {
            const errorMessage = err.error?.message || err.message || 'Error desconocido';

            if (errorMessage.includes('respuestas de usuarios')) {
              this.toastr.error(
                  'Este cuestionario no se puede eliminar porque ya tiene respuestas registradas',
                  'No se puede eliminar',
                  { timeOut: 5000 }
              );
            } else if (errorMessage.includes('activo')) {
              this.toastr.warning(
                  'No se puede eliminar el cuestionario activo. Primero active otro cuestionario.',
                  'Advertencia'
              );
            } else {
              this.toastr.error('Error al eliminar el cuestionario', 'Error');
            }
            console.error('Error completo:', err);
          }
        });
      }
    });
  }

  // ========== NAVEGAR AL BUILDER ==========
  goToBuilder(id?: number) {
    const target = id ? id : 'new';
    this.router.navigate(['/admin/surveys/builder', target]);
  }
}