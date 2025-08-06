import { createAction, props } from '@ngrx/store';
import { INewUserApiPayload, INewUserApiResponse, IVerifyNewUserApiResponse } from '../../../models/auth.api';

// reset registration state
export const resetRegistrationState = createAction(
  '[Register Component] resetRegistrationState'
);
// register new user
export const registerNewUserAction = createAction(
  '[Register Component] registerNewUser',
  props<{ payload: INewUserApiPayload }>()
);
export const registerNewUserSuccess = createAction(
  '[Register Component] registerNewUserSuccess',
  props<{ success: boolean; data: INewUserApiResponse }>()
);
export const registerNewUserFailure = createAction(
  '[Register Component] registerNewUserFailure',
  props<{ success: boolean; errorMessage: string }>()
);
// verify register new user
export const verifyNewUserAction = createAction(
  '[Register Component] verifyNewUser',
  props<{ code: string }>()
);
export const verifyNewUserSuccess = createAction(
  '[Register Component] verifyNewUserSuccess',
  props<{ success: boolean; data: IVerifyNewUserApiResponse }>()
);
export const verifyNewUserFailure = createAction(
  '[Register Component] verifyNewUserFailure',
  props<{ success: boolean; errorMessage: string }>()
);
