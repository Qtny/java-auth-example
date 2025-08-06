import { loginFeature } from './login.reducer';

export const {
  selectChallengeId,
  selectError,
  selectIsAuthenticated,
  selectLoading,
  selectLoginState,
  selectMfaType,
  selectSuccess,
  selectStep,
  selectToken,
  selectUser,
  selectMfaToken,
  selectIsMfaEnbaled,
} = loginFeature;
