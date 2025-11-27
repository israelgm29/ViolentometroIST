import {NivelRiesgo} from './nivel-riesgo.model';

export interface Violentometro {
  id: number;
  cedula: string;
  institutoId: number;
  respuestas: { preguntaId: number; valor: number }[];
  puntuacionTotal: number;
  nivelRiesgo: NivelRiesgo;
  usandoIa: boolean;
  fechaEvaluacion: Date;
}
