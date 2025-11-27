import {Instituto} from './instituto.model';

export interface Persona {
  cedula: string;
  nombreCompleto: string;
  rol: 'ESTUDIANTE' | 'DOCENTE';
  edad: number;
  instituto: Instituto;
}
