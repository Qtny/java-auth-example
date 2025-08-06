import { createFeature, createReducer, on } from '@ngrx/store';
import { IUser } from '../../../models/user.model';
import { verifyNewUserSuccess } from '../registration/registration.action';
import {
  initiateEmailMfaAction,
  initiateEmailMfaActionFailure,
  initiateEmailMfaActionSuccess,
  initiateTotpMfaAction,
  initiateTotpMfaActionFailure,
  initiateTotpMfaActionSuccess,
  loginAction,
  loginActionFailure,
  loginActionSuccess,
  resetLoginState,
  verifyLoginByEmailAction,
  verifyLoginByEmailActionSuccess,
  verifyLoginByTotpAction,
  verifyLoginByTotpActionFailure,
  verifyLoginByTotpActionSuccess,
} from './login.action';
import { EMfaChallengeType } from '../../../models/mfa.model';

export interface LoginState {
  success: boolean;
  loading: boolean;
  error: string | null;

  user: IUser | null;
  isMfaEnbaled: boolean | null;
  isAuthenticated: boolean;
  mfaType: EMfaChallengeType | null;
  mfaToken: string | null;
  token: string | null;
  challengeId: string | null;
  step: ELoginStep;
}

export enum ELoginStep {
  'NONE',
  'AWAITING_MFA',
  'COMPLETED',
}

const initialLoginState: LoginState = {
  success: false,
  loading: false,
  error: null,

  user: null,
  isAuthenticated: false,
  isMfaEnbaled: null,
  mfaType: null,
  mfaToken: null,
  token: null,
  challengeId: null,
  step: ELoginStep.NONE,
};

const loginReducer = createReducer(
  initialLoginState,
  // reset
  on(resetLoginState, (_) => initialLoginState),
  // get mfa token from registration
  on(verifyNewUserSuccess, (state, { data }) => ({
    ...state,
    mfaToken: data.token,
  })),
  // login
  on(loginAction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(loginActionSuccess, (state, { success, data }) => {
    if (data.code === 'MFA_NOT_ENABLED') {
      return {
        ...state,
        loading: false,
        success,
        isMfaEnbaled: false,
        mfaToken: data.token!,
      };
    }

    return {
      ...state,
      loading: false,
      isMfaEnbaled: true,
      success,
      mfaToken: data.token!,
      mfaType: data.type!,
      challengeId: data.challengeId ? data.challengeId : null,
    };
  }),
  on(loginActionFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  })),
  // initiate email mfa
  on(initiateEmailMfaAction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(initiateEmailMfaActionSuccess, (state, { success, data }) => ({
    ...state,
    loading: false,
    success,
    mfaType: EMfaChallengeType.EMAIL,
    challengeId: data.challengeId,
  })),
  on(initiateEmailMfaActionFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  })),
  // verify email mfa
  on(verifyLoginByEmailAction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(verifyLoginByEmailActionSuccess, (state, { success, data }) => ({
    ...state,
    loading: false,
    success,
    token: data.token,
  })),
  on(verifyLoginByTotpActionFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  })),
  // initiate totp mfa
  on(initiateTotpMfaAction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(initiateTotpMfaActionSuccess, (state, { success, data }) => ({
    ...state,
    loading: false,
    mfaType: EMfaChallengeType.TOTP,
    success,
  })),
  on(initiateTotpMfaActionFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  })),
  // verify totp mfa
  on(verifyLoginByTotpAction, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(verifyLoginByTotpActionSuccess, (state, { success, data }) => ({
    ...state,
    loading: false,
    success,
    token: data.token,
  })),
  on(verifyLoginByTotpActionFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  }))
);

export const loginFeature = createFeature({
  name: 'login',
  reducer: loginReducer,
});
