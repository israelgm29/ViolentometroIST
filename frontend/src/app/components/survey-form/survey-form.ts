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

@Component({
  selector: 'app-survey-form',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatSelectModule, MatDividerModule,
    MatChipsModule, RouterLink, MatTooltip
  ],
  templateUrl: './survey-form.html',
  styleUrl: './survey-form.css',
})
export class SurveyForm {
  private fb = inject(FormBuilder);
  private toastr = inject(ToastrService);
  private surveyService = inject(SurveyService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  surveyId = signal<number | null>(null);

  zones = toSignal(inject(ZoneService).getAllZones(), { initialValue: [] });

  isEditMode = computed(() => this.surveyId() !== null);

  surveyForm!: FormGroup;

  ngOnInit() {
    this.initForm();

    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.surveyId.set(Number(id));
      this.loadSurveyData(this.surveyId()!);
    } else {
      // Solo agregamos una pregunta vacía si estamos creando un nuevo cuestionario
      this.addQuestion();
    }
  }

  loadSurveyData(id: number) {
    this.surveyService.getSurveyById(id).subscribe({
      next: (survey) => {
        // Llenamos el formulario
        this.surveyForm.patchValue({
          title: survey.title,
          description: survey.description
        });

        // Limpiamos el array antes de cargar las preguntas
        this.questions.clear();

        // Mapeamos las preguntas desde el backend
        survey.questions.forEach(q => {
          this.questions.push(this.fb.group({
            id: [q.id || null], // ID de la pregunta si existe
            question: [q.question, Validators.required],
            idZone: [q.idZone, Validators.required],
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
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: [''],
      questions: this.fb.array([])
    });
  }

  get questions() {
    return this.surveyForm.get('questions') as FormArray;
  }

  addQuestion() {
    const questionGroup = this.fb.group({
      id: [null], // Para nuevas preguntas
      question: ['', Validators.required],
      idZone: [null, Validators.required],
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

  // Si borras la pregunta 2, la 3 pasa a ser la 2 automáticamente
  reorderQuestions() {
    this.questions.controls.forEach((control, index) => {
      control.patchValue({ questionNumber: index + 1 });
    });
  }

  saveSurvey() {
    if (this.surveyForm.valid) {
      const payload = {
        ...this.surveyForm.value,
        id: this.surveyId() // Incluimos el ID si estamos editando
      };

      this.surveyService.saveFullSurvey(payload).subscribe({
        next: (response) => {
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