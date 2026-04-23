import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CampaignDTO } from "../../models/campañing";

@Component({
  selector: 'app-campaign-preview-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './campaing-preview-dialog.html',
  styleUrls: ['./campaing-preview-dialog.css']
})
export class CampaignPreviewDialogComponent {

  readonly data: CampaignDTO = inject(MAT_DIALOG_DATA);
  private dialogRef = inject(MatDialogRef<CampaignPreviewDialogComponent>);

  onEdit() {
    this.dialogRef.close({ action: 'edit', campaign: this.data });
  }
}