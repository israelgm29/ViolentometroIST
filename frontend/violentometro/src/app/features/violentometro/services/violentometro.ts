import { Injectable, signal, inject, computed } from '@angular/core';
import { ApiService } from '../../../core/services/api'; // ✓ Ruta exacta
import { NivelRiesgo } from '../models/nivel-riesgo.model'; // ✓ Con .model
import { PreguntaFija } from '../models/pregunta-fija.model';
import { Violentometro } from '../models/violentometro.model';

@Injectable({ providedIn: 'root' })
export class ViolentometroService {
  private api = inject(ApiService);

  preguntas = signal<PreguntaFija[]>([]);
  respuestas = signal<Record<number, boolean>>({});

  // ✓ Añadido: Datos que faltaban
  institutoId = signal<number | null>(null);
  usandoIa = signal(false);

  // ✓ NUEVO: Signal computed para el nivel de riesgo actual (reactivo)
  nivelRiesgoActual = computed<NivelRiesgo>(() => {
    const puntos = this.puntuacionTotal();
    return this.getNivelRiesgo(puntos);
  });

  loadPreguntas() {
    this.api.getPreguntasFijas().subscribe({
      next: (preguntas) => {
        this.preguntas.set(preguntas);
      },
      error: (err) => {
        console.warn('⚠️ Backend no disponible, cargando preguntas de prueba', err);
        // Datos de prueba para poder probar la funcionalidad sin backend
        this.preguntas.set([
          { id: 1, codigo: 'P1', texto: '¿Te controla lo que haces?', nivelColor: 'AMARILLO', puntuacion: 1, categoria: 'Control' },
          { id: 2, codigo: 'P2', texto: '¿Te critica constantemente?', nivelColor: 'AMARILLO', puntuacion: 1, categoria: 'Control' },
          { id: 3, codigo: 'P3', texto: '¿Te humilla en público?', nivelColor: 'AMARILLO', puntuacion: 1, categoria: 'Humillación' },
          { id: 4, codigo: 'P4', texto: '¿Te aísla de tus amigos/familia?', nivelColor: 'NARANJA', puntuacion: 1, categoria: 'Aislamiento' },
          { id: 5, codigo: 'P5', texto: '¿Te amenaza con dejarte?', nivelColor: 'NARANJA', puntuacion: 1, categoria: 'Amenazas' },
          { id: 6, codigo: 'P6', texto: '¿Te amenaza con hacerse daño?', nivelColor: 'NARANJA', puntuacion: 1, categoria: 'Amenazas' },
          { id: 7, codigo: 'P7', texto: '¿Te ha empujado o zarandeado?', nivelColor: 'NARANJA', puntuacion: 1, categoria: 'Física' },
          { id: 8, codigo: 'P8', texto: '¿Te ha golpeado?', nivelColor: 'ROJO', puntuacion: 1, categoria: 'Física' },
          { id: 9, codigo: 'P9', texto: '¿Te obliga a hacer cosas que no quieres?', nivelColor: 'ROJO', puntuacion: 1, categoria: 'Control' },
          { id: 10, codigo: 'P10', texto: '¿Te amenaza con armas u objetos?', nivelColor: 'ROJO', puntuacion: 1, categoria: 'Amenazas' },
          { id: 11, codigo: 'P11', texto: '¿Has tenido que ir al hospital por sus acciones?', nivelColor: 'ROJO', puntuacion: 1, categoria: 'Física' },
          { id: 12, codigo: 'P12', texto: '¿Temes por tu vida?', nivelColor: 'ROJO', puntuacion: 1, categoria: 'Peligro' }
        ]);
      }
    });
  }

  toggleRespuesta(preguntaId: number, value: boolean) {
    this.respuestas.update(resp => ({
      ...resp,
      [preguntaId]: value
    }));
    console.log(`✅ Respuesta ${preguntaId}: ${value} | Puntos: ${this.puntuacionTotal()} | Nivel: ${this.nivelRiesgoActual()}`);
  }

  puntuacionTotal(): number { // ✓ Renombrado para coincidir con el modelo
    return Object.values(this.respuestas()).filter(Boolean).length;
  }

  getNivelRiesgo(puntos: number): NivelRiesgo {
    if (puntos === 0) return 'NEUTRO';
    if (puntos <= 3) return 'BAJO';
    if (puntos <= 7) return 'MEDIO';
    if (puntos <= 12) return 'ALTO';
    return 'CRITICO';
  }

  // ✓ NUEVO: Convierte al formato correcto para el backend
  private prepararRespuestasParaBackend(): { preguntaId: number; valor: number }[] {
    return Object.entries(this.respuestas())
      .filter(([, value]) => value)
      .map(([key]) => ({
        preguntaId: Number(key),
        valor: 1
      }));
  }

  // ✓ Método actualizado que coincide 100% con el modelo
  guardarViolentometro(cedula: string) {
    // Validación mínima
    if (!cedula || this.preguntas().length === 0) {
      console.error('❌ Datos incompletos');
      return;
    }

    const dto: Omit<Violentometro, 'id'> = { // ✓ Omit 'id' porque el backend lo genera
      cedula,
      institutoId: this.institutoId() ?? 1, // ✓ Valor por defecto si no está seteado
      respuestas: this.prepararRespuestasParaBackend(),
      puntuacionTotal: this.puntuacionTotal(),
      nivelRiesgo: this.getNivelRiesgo(this.puntuacionTotal()),
      usandoIa: this.usandoIa(),
      fechaEvaluacion: new Date()
    };

    this.api.saveViolentometro(dto as Violentometro).subscribe({
      next: () => console.log('✅ Violentómetro guardado'),
      error: err => console.error('❌ Error:', err)
    });
  }
}
