import { Component, signal, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViolentometroService } from '../../services/violentometro';

@Component({
  selector: 'app-face3d',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="face-container">
      <img 
        [src]="faceImagePath" 
        alt="Rostro Violentómetro"
        class="face-image"
        [class.level-neutro]="riskLevel() === 'neutro'"
        [class.level-bajo]="riskLevel() === 'bajo'"
        [class.level-medio]="riskLevel() === 'medio'"
        [class.level-alto]="riskLevel() === 'alto'"
      />
      <div class="glow-effect" 
           [class.active]="riskLevel() === 'alto'">
      </div>
    </div>
  `,
  styles: [`
    .face-container {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      z-index: -1;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      pointer-events: none;
    }

    .face-image {
      width: 60vw;
      max-width: 500px;
      height: auto;
      opacity: 0.25;
      filter: grayscale(0.3) brightness(1.1);
      transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
      animation: gentle-float 6s ease-in-out infinite;
    }

    /* Animación flotante suave */
    @keyframes gentle-float {
      0%, 100% { transform: translateY(0) scale(1); }
      50% { transform: translateY(-15px) scale(1.02); }
    }

    /* Niveles de riesgo */
    .face-image.level-neutro {
      filter: grayscale(0.8) brightness(0.9) contrast(0.9);
    }

    .face-image.level-bajo {
      filter: hue-rotate(40deg) saturate(1.5) brightness(1.2) contrast(1.1);
      opacity: 0.3;
    }

    .face-image.level-medio {
      filter: hue-rotate(15deg) saturate(2) brightness(1.1) contrast(1.2);
      opacity: 0.35;
      animation: gentle-pulse 3s ease-in-out infinite;
    }

    .face-image.level-alto {
      filter: hue-rotate(350deg) saturate(2.5) brightness(1.1) contrast(1.3);
      opacity: 0.4;
      animation: intense-pulse 2s ease-in-out infinite;
    }

    /* Animaciones de pulso */
    @keyframes gentle-pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.03); }
    }

    @keyframes intense-pulse {
      0%, 100% { transform: scale(1); opacity: 0.4; }
      50% { transform: scale(1.05); opacity: 0.5; }
    }

    /* Efecto de brillo para nivel alto */
    .glow-effect {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 70vw;
      max-width: 600px;
      height: 70vw;
      max-height: 600px;
      border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 50, 50, 0.3) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 1s ease-in-out;
      pointer-events: none;
      animation: glow-pulse 2s ease-in-out infinite;
    }

    .glow-effect.active {
      opacity: 1;
    }

    @keyframes glow-pulse {
      0%, 100% { transform: translate(-50%, -50%) scale(0.9); }
      50% { transform: translate(-50%, -50%) scale(1.1); }
    }

    /* Responsive */
    @media (max-width: 768px) {
      .face-image {
        width: 80vw;
        max-width: 350px;
      }
    }
  `]
})
export class Face3DComponent {
  private violentometroService = inject(ViolentometroService);

  riskLevel = signal<'neutro' | 'bajo' | 'medio' | 'alto'>('neutro');
  faceImagePath = '/images/violentometro-face.png';

  constructor() {
    // Conectar con el servicio para actualizar automáticamente
    effect(() => {
      const nivelRiesgo = this.violentometroService.nivelRiesgoActual();
      console.log('🎨 Cambio de nivel de riesgo detectado:', nivelRiesgo);
      this.updateRisk(nivelRiesgo);
    }, { allowSignalWrites: true });
  }

  updateRisk(nivel: 'NEUTRO' | 'BAJO' | 'MEDIO' | 'ALTO' | 'CRITICO') {
    // Convertir del formato del servicio al formato del componente
    const levelMap: Record<string, 'neutro' | 'bajo' | 'medio' | 'alto'> = {
      'NEUTRO': 'neutro',
      'BAJO': 'bajo',
      'MEDIO': 'medio',
      'ALTO': 'alto',
      'CRITICO': 'alto' // CRITICO se trata como alto visualmente
    };
    const newLevel = levelMap[nivel] || 'neutro';
    console.log(`👤 Actualizando rostro: ${nivel} → ${newLevel}`);
    this.riskLevel.set(newLevel);
  }
}
