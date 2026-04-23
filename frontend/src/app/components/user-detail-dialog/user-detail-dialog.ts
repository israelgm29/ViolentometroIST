import {Component, Inject} from '@angular/core';
import {CommonModule} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialogModule} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatListModule} from "@angular/material/list";
import {MatDividerModule} from "@angular/material/divider";
import {SysUserInterface} from "../../models/sys-user";

@Component({
  selector: 'app-user-detail-dialog',
  imports: [CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatDividerModule],
  templateUrl: './user-detail-dialog.html',
  styleUrl: './user-detail-dialog.css',
})
export class UserDetailDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public user: SysUserInterface) {}
}
