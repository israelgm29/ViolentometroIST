import { Component, Inject, OnInit, OnDestroy, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule }  from '@angular/material/button';
import { MatIconModule }    from '@angular/material/icon';
import { CommonModule }     from '@angular/common';

@Component({
  selector: 'app-inactivity-warning-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <div class="inactivity-dialog">
      <div class="inactivity-icon">
        <mat-icon>schedule</mat-icon>
      </div>
      <h2>¿Sigues ahí?</h2>
      <p>Tu sesión se cerrará por inactividad en</p>
      <div class="countdown">{{ secondsLeft() }}s</div>
      <p class="hint">Haz clic en continuar para mantener tu sesión activa.</p>
      <div class="inactivity-actions">
        <button mat-button (click)="close(false)">
          <mat-icon>logout</mat-icon>
          Cerrar sesión
        </button>
        <button mat-raised-button color="primary" (click)="close(true)">
          <mat-icon>check</mat-icon>
          Continuar sesión
        </button>
      </div>
    </div>
  `,
  styles: [`
    .inactivity-dialog {
      text-align:      center;
      padding:         var(--space-xl, 2rem);
      display:         flex;
      flex-direction:  column;
      align-items:     center;
      gap:             var(--space-md, 1rem);
    }
    .inactivity-icon mat-icon {
      font-size: 48px;
      width:     48px;
      height:    48px;
      color:     var(--color-warning, #d97706);
    }
    h2 { margin: 0; font-size: 1.5rem; font-weight: 700; }
    p  { margin: 0; color: #6b7280; }
    .countdown {
      font-size:   3rem;
      font-weight: 800;
      color:       var(--color-danger, #dc2626);
      line-height: 1;
    }
    .hint { font-size: 0.875rem; }
    .inactivity-actions {
      display: flex;
      gap:     1rem;
      margin-top: 0.5rem;
    }
  `]
})
export class InactivityWarningDialogComponent implements OnInit, OnDestroy {

  secondsLeft = signal(0);
  private interval: any;

  constructor(
      @Inject(MAT_DIALOG_DATA) public data: { seconds: number },
      private dialogRef: MatDialogRef<InactivityWarningDialogComponent>
  ) {
    this.secondsLeft.set(data.seconds);
  }

  ngOnInit() {
    this.interval = setInterval(() => {
      this.secondsLeft.update(s => s - 1);
      if (this.secondsLeft() <= 0) {
        clearInterval(this.interval);
      }
    }, 1000);
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

  close(keepSession: boolean) {
    this.dialogRef.close(keepSession);
  }
}