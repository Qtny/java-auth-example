import { Component, inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-error-dialog-component',
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './error-dialog-component.html',
})
export class ErrorDialogComponent {
  readonly data = inject<{ title: string; description: string }>(
    MAT_DIALOG_DATA
  );
}
