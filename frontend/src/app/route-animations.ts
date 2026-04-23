import {
    trigger,
    transition,
    style,
    query,
    group,
    animate,
} from '@angular/animations';

export const fadeAnimation = trigger('routeAnimations', [
    transition('* <=> *', [
        query(':enter, :leave', [
            style({
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                opacity: 0,
            }),
        ], { optional: true }),
        query(':enter', [
            style({ opacity: 0, transform: 'translateY(20px)' }),
        ], { optional: true }),
        group([
            query(':leave', [
                animate('300ms ease-out', style({ opacity: 0, transform: 'translateY(-20px)' })),
            ], { optional: true }),
            query(':enter', [
                animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
            ], { optional: true }),
        ]),
    ]),
]);
