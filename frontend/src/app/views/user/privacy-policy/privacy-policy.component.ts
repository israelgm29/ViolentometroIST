import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon'; // Assuming you use MatIcon

@Component({
  selector: 'app-privacy-policy',
  standalone: true,
  imports: [CommonModule, MatIconModule], // Add MatIconModule if you use mat-icon in the template
  templateUrl: './privacy-policy.component.html',
  styleUrls: ['./privacy-policy.component.scss']
})
export class PrivacyPolicyComponent {
  lastUpdatedDate: Date = new Date();
}
