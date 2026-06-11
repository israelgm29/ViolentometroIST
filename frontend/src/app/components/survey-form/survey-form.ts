import {Component, computed, inject, signal} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatSelectModule} from "@angular/material/select";
import {MatDividerModule} from "@angular/material/divider";
import {SurveyService} from "../../services/survey.service";
import {ZoneService} from "../../services/zone.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {MatChipsModule} from "@angular/material/chips";
import {MatTooltip} from "@angular/material/tooltip";
import {toSignal} from "@angular/core/rxjs-interop";
import {ToastrService} from "ngx-toastr";
import {ViolenceZoneInterface} from "../../models/zone";

@Component({
  selector: 'app-survey-form',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatSelectModule, MatDividerModule,
    MatChipsModule, RouterLink, MatTooltip
  ],
  templateUrl: './survey-form.html',
  styleUrl: './survey-form.scss',
})
export class SurveyForm {
  private fb            = inject(FormBuilder);
  private toastr        = inject(ToastrService);
  private surveyService = inject(SurveyService);
  private zoneService   = inject(ZoneService);
  private route         = inject(ActivatedRoute);
  private router        = inject(Router);

  surveyId = signal<number | null>(null);

  zones = toSignal(this.zoneService.getAllZones(), { initialValue: [] as ViolenceZoneInterface[] });

  isEditMode = computed(() => this.surveyId() !== null);

  surveyForm!: FormGroup;

  ngOnInit() {
    this.initForm();

    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.surveyId.set(Number(id));
      this.loadSurveyData(this.surveyId()!);
    } else {
      this.addQuestion();
    }
  }

  loadSurveyData(id: number) {
    this.surveyService.getSurveyById(id).subscribe({
      next: (survey) => {
        this.surveyForm.patchValue({
          title:       survey.title,
          description: survey.description
        });

        this.questions.clear();

        survey.questions.forEach(q => {
          this.questions.push(this.fb.group({
            id:             [q.id || null],
            question:       [q.question, Validators.required],
            idZone:         [q.idZone,   Validators.required],
            questionNumber: [q.questionNumber]
          }));
        });
      },
      error: (err) => {
        this.toastr.error('Error al cargar el cuestionario', 'Error');
        console.error(err);
        this.router.navigate(['/admin/surveys']);
      }
    });
  }

  initForm() {
    this.surveyForm = this.fb.group({
      title:       ['', [Validators.required, Validators.minLength(5)]],
      description: [''],
      questions:   this.fb.array([])
    });
  }

  get questions(): FormArray {
    return this.surveyForm.get('questions') as FormArray;
  }

  addQuestion() {
    const questionGroup = this.fb.group({
      id:             [null],
      question:       ['', Validators.required],
      idZone:         [null, Validators.required],
      questionNumber: [this.questions.length + 1]
    });
    this.questions.push(questionGroup);
  }

  removeQuestion(index: number) {
    if (this.questions.length > 1) {
      this.questions.removeAt(index);
      this.reorderQuestions();
    }
  }

  reorderQuestions() {
    this.questions.controls.forEach((control, index) => {
      control.patchValue({ questionNumber: index + 1 });
    });
  }

  // ── Helpers para el panel lateral ────────────────────

  /** Zonas únicas que ya tienen al menos una pregunta asignada */
  zonesUsed(): ViolenceZoneInterface[] {
    const usedIds = new Set<number>(
      this.questions.controls
        .map(c => c.get('idZone')?.value)
        .filter((v): v is number => v !== null && v !== undefined)
    );
    return (this.zones() as ViolenceZoneInterface[]).filter(z => usedIds.has(z.id));
  }

  /** Cuántas preguntas usa una zona específica */
  countByZone(zoneId: number): number {
    return this.questions.controls.filter(c => c.get('idZone')?.value === zoneId).length;
  }

  /** Devuelve el objeto Zone dado un idZone, o null si no está seleccionado */
  getZoneName(idZone: number | null): ViolenceZoneInterface | null {
    if (idZone == null) return null;
    return (this.zones() as ViolenceZoneInterface[]).find(z => z.id === idZone) ?? null;
  }

  // ── Guardar ──────────────────────────────────────────

  saveSurvey() {
    if (this.surveyForm.valid) {
      const payload = {
        ...this.surveyForm.value,
        id: this.surveyId()
      };

      this.surveyService.saveFullSurvey(payload).subscribe({
        next: () => {
          const message = this.isEditMode()
            ? 'Cuestionario actualizado con éxito'
            : 'Cuestionario creado con éxito';
          this.toastr.success(message, 'Éxito');
          this.router.navigate(['/admin/surveys']);
        },
        error: (err) => {
          const errorMsg = this.isEditMode()
            ? 'Error al actualizar el cuestionario'
            : 'Error al crear el cuestionario';
          this.toastr.error(errorMsg, 'Error');
          console.error(err);
        }
      });
    } else {
      this.toastr.warning('Por favor, completa todos los campos requeridos', 'Formulario incompleto');
      this.surveyForm.markAllAsTouched();
    }
  }
}