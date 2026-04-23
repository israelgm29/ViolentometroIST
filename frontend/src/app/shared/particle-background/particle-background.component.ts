import {Component, OnInit, OnDestroy} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NgxParticlesModule} from '@tsparticles/angular';
import {loadSlim} from '@tsparticles/slim';
import type {Container, Engine} from '@tsparticles/engine';

@Component({
    selector: 'app-particle-background',
    standalone: true,
    imports: [NgxParticlesModule, CommonModule],
    template: `
        <ngx-particles
                id="tsparticles"
                [options]="particlesOptions"
                [particlesInit]="particlesInit"
                (particlesLoaded)="onParticlesLoaded($event)"
        ></ngx-particles>

        <div class="risk-indicator">
            <img loading="lazy" decoding="async" width="100" height="100"
                 src="https://fundacionaliada.com/wp-content/uploads/2023/10/QR-NUEVO.jpeg" alt="" title="QR NUEVO"
                 srcset="https://fundacionaliada.com/wp-content/uploads/2023/10/QR-NUEVO.jpeg 1600w, https://fundacionaliada.com/wp-content/uploads/2023/10/QR-NUEVO-1280x1280.jpeg 1280w, https://fundacionaliada.com/wp-content/uploads/2023/10/QR-NUEVO-980x980.jpeg 980w, https://fundacionaliada.com/wp-content/uploads/2023/10/QR-NUEVO-480x480.jpeg 480w"
                 sizes="(min-width: 0px) and (max-width: 480px) 480px, (min-width: 481px) and (max-width: 980px) 980px, (min-width: 981px) and (max-width: 1280px) 1280px, (min-width: 1281px) 1600px, 100vw"
                 class="wp-image-454">
        </div>
    `,
    styleUrls: ['./particle-background.component.scss']
})
export class ParticleBackgroundComponent implements  OnInit, OnDestroy {

    private container: Container | undefined;

    particlesOptions: any = {
        background: {
            color: {
                value: '#72467c'
            }
        },
        fpsLimit: 60,

        interactivity: {
            events: {
                onClick: {enable: false},
                onHover: {enable: false},
                resize: {
                    delay: 0.5,
                    enable: true
                }
            },
            modes: {}
        },

        particles: {
            color: {
                value: '#ffffff'
            },

            links: {
                enable: false
            },

            collisions: {
                enable: true,  // Activado para detección
                mode: 'bounce',
                overlap: {
                    enable: false,  // No permitir superposición
                    retries: 100    // Intentos para encontrar posición sin superposición
                }
            },

            move: {
                enable: false  // Desactivado: círculos estáticos
            },

            number: {
                density: {
                    enable: true,
                    width: 1920,
                    height: 1080,
                    factor: 2000  // Factor alto para mayor dispersión
                },
                value: 20  // Reducido para evitar superposición
            },

            opacity: {
                value: {min: 0.4, max: 0.8},
                animation: {
                    enable: false  // Sin animación de opacidad
                }
            },

            shape: {
                type: 'image',
                options: {
                    image: [
                        {src: 'assets/images/image11-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image12-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image13-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image14-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image15-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image16-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image17-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image18-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image19-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image110-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image111-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image112-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image113-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image114-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image115-circle.png', width: 100, height: 100},
                        {src: 'assets/images/image116-circle.png', width: 100, height: 100}
                    ]
                }
            },

            size: {
                value: 100,  // Tamaño fijo para mejor control
                animation: {
                    enable: false
                }
            },

            rotate: {
                value: {min: 0, max: 360},  // Rotación aleatoria inicial
                direction: 'random',
                animation: {
                    enable: true,  // Activada: círculos giran
                    speed: 2,      // Velocidad lenta y suave
                    sync: false    // Cada uno gira a su ritmo
                }
            },

            // Distribuir círculos uniformemente
            position: {
                x: {random: {enable: true}},
                y: {random: {enable: true}}
            }
        },

        detectRetina: true,

        responsive: [
            {
                maxWidth: 1024,
                options: {
                    particles: {
                        number: {
                            value: 15,
                            density: {
                                enable: true,
                                factor: 2000
                            }
                        },
                        size: {value: 90}
                    }
                }
            },
            {
                maxWidth: 768,
                options: {
                    particles: {
                        number: {
                            value: 12,
                            density: {
                                enable: true,
                                factor: 1800
                            }
                        },
                        size: {value: 80}
                    }
                }
            },
            {
                maxWidth: 480,
                options: {
                    particles: {
                        number: {
                            value: 8,
                            density: {
                                enable: true,
                                factor: 1500
                            }
                        },
                        size: {value: 70}
                    }
                }
            }
        ]
    };

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        if (this.container) {
            this.container.destroy();
        }
    }

    particlesInit = async (engine: Engine): Promise<void> => {
        await loadSlim(engine);
    };

    async onParticlesLoaded(container: Container): Promise<void> {
        this.container = container;

    }

    public setParticleCount(count: number): void {
        if (!this.container) return;

        const options = this.container.options;
        if (options.particles?.number) {
            options.particles.number.value = count;
            this.container.refresh();
        }
    }
}