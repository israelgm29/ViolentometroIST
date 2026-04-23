import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy, effect, inject } from '@angular/core';
import { AuthService } from '../../../services/auth.service';

@Directive({
    selector: '[appHasRole]',
    standalone: true
})
export class HasRoleDirective implements OnInit, OnDestroy {
    private authService = inject(AuthService);
    private requiredRoles: string[] = [];
    private hasView = false;
    private effectRef: any = null;

    @Input('appHasRole')
    set appHasRole(roles: string | string[]) {
        this.requiredRoles = Array.isArray(roles) ? roles : [roles];
        this.updateView();
    }

    constructor(
        private templateRef: TemplateRef<any>,
        private viewContainer: ViewContainerRef
    ) {}

    ngOnInit(): void {
        // Usar effect() de Angular para reaccionar a cambios en signals
        this.effectRef = effect(() => {
            // Leer el signal para que effect se ejecute cuando cambie
            const user = this.authService.user();
            this.updateView();
        });
    }

    ngOnDestroy(): void {
        // Limpiar el effect al destruir la directiva
        this.effectRef?.destroy();
    }

    private updateView(): void {
        const hasAccess = this.authService.hasAnyRole(...this.requiredRoles);

        if (hasAccess && !this.hasView) {
            this.viewContainer.createEmbeddedView(this.templateRef);
            this.hasView = true;
        } else if (!hasAccess && this.hasView) {
            this.viewContainer.clear();
            this.hasView = false;
        }
    }
}