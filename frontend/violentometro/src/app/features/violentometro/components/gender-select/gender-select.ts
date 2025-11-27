import { Component, output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-gender-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gender-select.html', // ✓ Usa templateUrl
  styleUrls: ['./gender-select.scss']   // ✓ Usa styleUrls
})
export class GenderSelectComponent {
  onSelect = output<string>();

  select(gender: string) {
    this.onSelect.emit(gender);
  }
}
