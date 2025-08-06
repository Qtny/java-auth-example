import { createFeature, createReducer, on } from '@ngrx/store';
import {
  registerNewUserAction,
  registerNewUserFailure,
  registerNewUserSuccess,
  resetRegistrationState,
  verifyNewUserAction,
  verifyNewUserFailure,
  verifyNewUserSuccess,
} from './registration.action';

export interface RegistrationState {
  success: boolean;
  loading: boolean;
  error: string | null;

  token: string | null;
  challengeId: string | null;
  step: ERegistrationStep;
}

export enum ERegistrationStep {
  NONE,
  AWAITING_OTP,
  COMPLETE,
}

const initialRegistrationState: RegistrationState = {
  success: false,
  loading: false,
  error: null,

  token: null,
  challengeId: null,
  step: ERegistrationStep.NONE,
};

const registrationReducer = createReducer(
  initialRegistrationState,
  // reset
  on(resetRegistrationState, (_) => initialRegistrationState),
  // register
  on(registerNewUserAction, (state) => ({ ...state, loading: true })),
  on(registerNewUserSuccess, (state, { success, data }) => ({
    ...state,
    loading: false,
    success,
    token: data.token,
    challengeId: data.challengeId,
    step: ERegistrationStep.AWAITING_OTP,
  })),
  on(registerNewUserFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: JSON.stringify(errorMessage),
  })),
  // verify register
  on(verifyNewUserAction, (state) => ({ ...state, loading: true })),
  on(verifyNewUserSuccess, (state, { success, data }) => {
    if (data.code === 'MFA_NOT_ENABLED') {
      return {
        ...state,
        loading: false,
        success,
        step: ERegistrationStep.COMPLETE,
      };
    }

    return {
      ...state,
      loading: false,
      success,
    };
  }),
  on(verifyNewUserFailure, (state, { success, errorMessage }) => ({
    ...state,
    loading: false,
    success,
    error: errorMessage,
  }))
);

export const registrationFeature = createFeature({
  name: 'registration',
  reducer: registrationReducer,
});
