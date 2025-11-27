export interface PreguntaFija {
  id: number;
  codigo: string;
  texto: string;
  nivelColor: 'AMARILLO' | 'NARANJA' | 'ROJO';
  puntuacion: number;
  categoria: string;
}
