import {Component, Inject} from '@angular/core';
import {MatProgressBar, MatProgressBarModule} from "@angular/material/progress-bar";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef
} from "@angular/material/dialog";
import {QuestionZone} from "../../models/question-zone";
import {MatIconModule} from "@angular/material/icon";
import {MatDividerModule} from "@angular/material/divider";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-question-detail-dialog',
  imports: [
    MatDividerModule,
    MatProgressBarModule,
    MatIconModule,
    MatDialogModule,
    MatButton
  ],
  templateUrl: './question-detail-dialog.html',
  styleUrl: './question-detail-dialog.css',
})
export class QuestionDetailDialog {
  constructor(
      public dialogRef: MatDialogRef<QuestionDetailDialog>,
      @Inject(MAT_DIALOG_DATA) public data: QuestionZone // Recibe el objeto completo
  ) {}

  getColorHex(colorName: string): string {
    if (!colorName) return '#ccc';

    const colors: { [key: string]: string } = {
      'verde': '#2e7d32',
      'amarillo': '#fbc02d',
      'naranja': '#ef6c00',
      'rojo': '#d32f2f'
    };
    return colors[colorName.toLowerCase()] || colorName;
  }

  close(): void {
    this.dialogRef.close();
  }
}
