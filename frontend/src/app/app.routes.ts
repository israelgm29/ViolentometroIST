import {Routes} from '@angular/router';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {authGuard, roleGuard} from './guards/auth.guard';

// Roles disponibles
const ADMIN = 'ROLE_ADMIN';
const ANALYST = 'ROLE_ANALYST';
const WELFARE = 'ROLE_WELFARE';

export const routes: Routes = [

    // ══════════════════════════════════════════
    // RUTAS PÚBLICAS — sin guard
    // ══════════════════════════════════════════
    {
        path: '',
        component: UserLayoutComponent,
        children: [
            {
                path: '',
                loadComponent: () => import('./views/user/home/home.component').then(m => m.HomeComponent),
                data: {animation: 'HomePage'}
            },
            {
                path: 'violentometer',
                loadComponent: () => import('./components/violentometro-form/violentometro-form').then(m => m.ViolentometroFormComponent),
                data: {animation: 'ViolentometroPage'}
            },
            {
                path: 'quiz',
                loadComponent: () => import('./views/user/question-container/questions-container.component').then(m => m.QuestionsContainerComponent),
                data: {animation: 'QuizPage'}
            },
            {
                path: 'about',
                loadComponent: () => import('./views/user/about/about.component').then(m => m.AboutComponent),
                data: {animation: 'AboutPage'}
            },
            {
                path: 'welfare',
                loadComponent: () => import('./views/user/contact/contact.component').then(m => m.ContactComponent),
                data: {animation: 'ContactPage'}
            },
            {
                path: 'help',
                loadComponent: () => import('./views/user/help/help.component').then(m => m.HelpComponent),
                data: {animation: 'HelpPage'}
            },
            {
                path: 'campaigns',
                loadComponent: () => import('./views/user/campaigns/campaigns.component').then(m => m.CampaignsComponent),
                data: {animation: 'CampaignsPage'}
            }
        ]
    },

    // ══════════════════════════════════════════
    // LOGIN — público
    // ══════════════════════════════════════════
    {
        path: 'admin/login',
        loadComponent: () => import('./views/login/login').then(m => m.Login),
        data: {animation: 'AdminLoginPage'}
    },

    // ══════════════════════════════════════════
    // RUTAS ADMIN — protegidas por rol
    // ══════════════════════════════════════════
    {
        path: 'admin',
        component: AdminLayoutComponent,
        canActivate: [authGuard],
        children: [

            // ======================================
            // RUTAS: ADMIN + WELFARE
            // ======================================
            {
                path: 'dashboard',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/dashboard/dashboard').then(m => m.Dashboard),
                data: {animation: 'AdminDashboardPage'}
            },
            {
                path: 'questions',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/questions/questions').then(m => m.QuestionsComponent),
                data: {animation: 'AdminQuestionsPage'}
            },
            {
                path: 'surveys',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/survey/survey').then(m => m.SurveyManagerComponent),
                data: {animation: 'AdminSurveysPage'}
            },
            {
                path: 'surveys/builder/:id',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./components/survey-form/survey-form').then(m => m.SurveyForm),
                data: {animation: 'AdminSurveyBuilderPage'}
            },
            {
                path: 'report/general',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./components/general-report/general-report').then(m => m.GeneralReport)
            },
            {
                path: 'report/:type',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/report-detail/report-detail').then(m => m.ReportDetail),
                data: {animation: 'AdminReportDetailPage'}
            },
            {
                path: 'campaigns',
                canActivate: [roleGuard(ADMIN, WELFARE)],
                loadComponent: () => import('./views/root/campaing/campaing').then(m => m.CampaingComponent),
                data: {animation: 'AdminCampaignsPage'}
            },

            // ======================================
            // RUTAS: ADMIN + ANALYST + WELFARE
            // ======================================
            {
                path: 'profile',
                canActivate: [roleGuard(ADMIN, ANALYST, WELFARE)],
                loadComponent: () => import('./views/root/user-profile/user-profile').then(m => m.UserProfile),
                data: {animation: 'AdminProfilePage'}
            },
            {
                path: 'users',
                canActivate: [roleGuard(ADMIN, ANALYST, WELFARE)],
                loadComponent: () => import('./views/root/app-user/app-user').then(m => m.AppUser),
                data: {animation: 'AdminUsersPage'}
            },

            // ======================================
            // RUTAS: SOLO ADMIN
            // ======================================
            {
                path: 'institutes',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/institutes/institutes').then(m => m.Institutes),
                data: {animation: 'AdminInstitutesPage'}
            },
            {
                path: 'violence-zones',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/violence-zone/violence-zone').then(m => m.ViolenceZone),
                data: {animation: 'AdminViolenceZonesPage'}
            },
            {
                path: 'sys-users',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/sys-user/sys-user').then(m => m.SysUser),
                data: {animation: 'AdminSysUsersPage'}
            },
            {
                path: 'settings',
                canActivate: [roleGuard(ADMIN)],
                loadComponent: () => import('./views/root/catalog-manager/catalog-manager').then(m => m.CatalogManager),
                data: {animation: 'AdminSettingsPage'}
            },

            {path: '', redirectTo: 'dashboard', pathMatch: 'full'}
        ]
    },

    {path: '**', redirectTo: ''}
];