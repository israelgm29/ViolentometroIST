import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {TitleCasePipe} from "@angular/common";
import {MatButton} from "@angular/material/button";

@Component({
    selector: 'app-zone-detail-dialog',
    standalone: true,
    imports: [
        MatDialogModule,
        MatIcon,
        TitleCasePipe,
        MatButton
    ],
    templateUrl: './zone-detail-dialog.html',
    styleUrl: './zone-detail-dialog.css',
})
export class ZoneDetailDialog {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}
}