import {Routes} from '@angular/router';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {authGuard, roleGuard} from './guards/auth.guard';

const ADMIN = 'ROLE_ADMIN';
const ANALYST = 'ROLE_ANALYST';
const WELFARE = 'ROLE_WELFARE';

export const routes: Routes = [

    // ══════════════════════════════════════════
    // RUTAS PÚBLICAS
    // ══════════════════════════════════════════
    {
        path: '',
        component: UserLayoutComponent,
        children: [
            {
                path: '',
                loadComponent: () => import('./views/user/home/home.component').then(m => m.HomeComponent)
            },
            {
                path: 'violentometer',
                loadComponent: () => import('./components/violentometro-form/violentometro-form').then(m => m.ViolentometroFormComponent)
            },
            {
                path: 'quiz',
                loadComponent: () => import('./views/user/question-container/questions-container.component').then(m => m.QuestionsContainerComponent),

            },
            {
                path: 'welfare',
                loadComponent: () => import('./views/user/contact/contact.component').then(m => m.ContactComponent),

            },
            {
                path: 'help',
                loadComponent: () => import('./views/user/help/help.component').then(m => m.HelpComponent),

            },
            {
                path: 'campaigns',
                loadComponent: () => import('./views/user/campaigns/campaigns.component').then(m => m.CampaignsComponent),

            },
            {
                path: 'privacy-policy',
                loadComponent: () => import('./views/user/privacy-policy/privacy-policy.component').then(m => m.PrivacyPolicyComponent),


            }
        ]
    },

    // ══════════════════════════════════════════
    // LOGIN
    // ══════════════════════════════════════════
    {
        path: 'admin/login',
        loadComponent: () => import('./views/login/login').then(m => m.Login),

    },

    // ══════════════════════════════════════════
    // RUTAS ADMIN
    // ══════════════════════════════════════════
    {
        path: 'admin',
        component: AdminLayoutComponent,
        canActivate: [authGuard],
        children: [

            // ADMIN + WELFARE
            {
                path: 'dashboard',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/dashboard/dashboard').then(m => m.Dashboard),

            },
            {
                path: 'questions',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/questions/questions').then(m => m.QuestionsComponent),

            },
            {
                path: 'surveys',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/survey/survey').then(m => m.SurveyManagerComponent),

            },
            {
                path: 'surveys/builder/:id',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./components/survey-form/survey-form').then(m => m.SurveyForm),

            },
            {
                path: 'campaigns',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/campaing/campaing').then(m => m.CampaingComponent),

            },
            {
                path: 'reports',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/reports/reports.component').then(m => m.ReportsComponent),

            },

            // ADMIN + ANALYST + WELFARE
            {
                path: 'profile',
                canActivate: [roleGuard(ADMIN, ANALYST, WELFARE)],
                loadComponent: () => import('./views/root/user-profile/user-profile').then(m => m.UserProfile),

            },
            {
                path: 'users',
                canActivate: [roleGuard(ADMIN, ANALYST, WELFARE)],
                loadComponent: () => import('./views/root/app-user/app-user').then(m => m.AppUser),

            },

            // SOLO ADMIN
            {
                path: 'institutes',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/institutes/institutes').then(m => m.Institutes),

            },
            {
                path: 'violence-zones',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/violence-zone/violence-zone').then(m => m.ViolenceZone),

            },
            {
                path: 'sys-users',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/sys-user/sys-user').then(m => m.SysUser),

            },
            {
                path: 'settings',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/catalog-manager/catalog-manager').then(m => m.CatalogManager),

            },

            {path: '', redirectTo: 'dashboard', pathMatch: 'full'}
        ]
    },

    {path: '**', redirectTo: ''}
];