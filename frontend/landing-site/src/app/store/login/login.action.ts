import { createAction, props } from '@ngrx/store';
import { IInitiateEmailMfaResponse, ILoginPayload, ILoginResponse, IVerifyLoginByEmailPayload, IVerifyLoginByTotpPayload, IVerifyLoginResponse } from '../../../models/auth.api';

// reset login state
export const resetLoginState = createAction(
  '[Login Component] resetLoginState'
);
// login
export const loginAction = createAction(
  '[Login Component] login',
  props<{ email: string, password: string }>()
);
export const loginActionSuccess = createAction(
  '[Login Component] loginSuccess',
  props<{ success: boolean; data: ILoginResponse }>()
);
export const loginActionFailure = createAction(
  '[Login Component] loginFailure',
  props<{ success: boolean; errorMessage: string }>()
);
// initiate email mfa
export const initiateEmailMfaAction = createAction(
  '[Login Component] initiateEmailMfa',
);
export const initiateEmailMfaActionSuccess = createAction(
  '[Login Component] initiateEmailMfaSuccess',
  props<{ success: boolean; data: IInitiateEmailMfaResponse }>()
);
export const initiateEmailMfaActionFailure = createAction(
  '[Login Component] initiateEmailMfaFailure',
  props<{ success: boolean; errorMessage: string }>()
);
// initiate email mfa
export const initiateTotpMfaAction = createAction(
  '[Login Component] initiateTotpMfa',
);
export const initiateTotpMfaActionSuccess = createAction(
  '[Login Component] initiateTotpMfaSuccess',
  props<{ success: boolean; data: null }>()
);
export const initiateTotpMfaActionFailure = createAction(
  '[Login Component] initiateTotpMfaFailure',
  props<{ success: boolean; errorMessage: string }>()
);
// verify login
export const verifyLoginByEmailAction = createAction(
  '[Login Component] verifyLoginByEmail',
  props<{ code: string }>()
);
export const verifyLoginByEmailActionSuccess = createAction(
  '[Login Component] verifyLoginByEmailSuccess',
  props<{ success: boolean; data: IVerifyLoginResponse }>()
);
export const verifyLoginByEmailActionFailure = createAction(
  '[Login Component] vverifyLoginByEmailFailure',
  props<{ success: boolean; errorMessage: string }>()
);
// verify totp login
export const verifyLoginByTotpAction = createAction(
  '[Login Component] verifyLoginByTotp',
  props<{ code: string }>()
);
export const verifyLoginByTotpActionSuccess = createAction(
  '[Login Component] verifyLoginByTotpSuccess',
  props<{ success: boolean; data: IVerifyLoginResponse }>()
);
export const verifyLoginByTotpActionFailure = createAction(
  '[Login Component] verifyLoginByTotpFailure',
  props<{ success: boolean; errorMessage: string }>()
);