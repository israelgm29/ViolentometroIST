import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import {RouterLink} from "@angular/router";

@Component({
    selector: 'app-footer',
    standalone: true,
    imports: [CommonModule, MatIconModule, RouterLink],
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
    currentYear = new Date().getFullYear();
}
