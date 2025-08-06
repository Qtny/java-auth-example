import { createAction, props } from '@ngrx/store';

export const openErrorDialogAction = createAction(
  '[Global Component] openErrorDialog',
  props<{ title: string; description: string }>()
);
