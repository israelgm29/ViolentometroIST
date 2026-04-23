import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideIonicAngular } from '@ionic/angular/standalone';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';
import { provideToastr } from 'ngx-toastr';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),

        provideHttpClient(withInterceptors([authInterceptor])),

        provideIonicAngular({}),

        provideAnimations(),

        provideToastr({
            positionClass: 'toast-top-right',
            preventDuplicates: true,
            closeButton: true,
        }),

        provideNativeDateAdapter(),

        {
            provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
            useValue: {
                appearance: 'outline',
                subscriptSizing: 'dynamic'
            }
        }
    ]
};