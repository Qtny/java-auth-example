import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { openErrorDialogAction } from './global.action';
import { tap } from 'rxjs';
import { ErrorDialogComponent } from '../../shared/error-dialog-component/error-dialog-component';
import { MatDialog } from '@angular/material/dialog';

@Injectable()
export class GlobalEffect {
  private readonly actions$ = inject(Actions);
  private readonly dialog = inject(MatDialog);

  openErrorDialog = createEffect(
    () =>
      this.actions$.pipe(
        ofType(openErrorDialogAction),
        tap(({ title, description }) => {
          this.dialog.open(ErrorDialogComponent, {
            data: { title, description },
          });
        })
      ),
    { dispatch: false }
  );
}
