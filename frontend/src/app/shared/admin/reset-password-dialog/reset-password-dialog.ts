import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-reset-password-dialog',
  imports: [MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, ReactiveFormsModule, MatIcon],
  templateUrl: './reset-password-dialog.html',
  styleUrl: './reset-password-dialog.css',
})
export class ResetPasswordDialog {
  passwordForm: FormGroup;
  hide = true;

  constructor(
      private fb: FormBuilder,
      public dialogRef: MatDialogRef<ResetPasswordDialog>,
      @Inject(MAT_DIALOG_DATA) public data: { name: string }
  ) {
    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSave() {
    if (this.passwordForm.valid) {
      this.dialogRef.close(this.passwordForm.value.newPassword);
    }
  }
}
