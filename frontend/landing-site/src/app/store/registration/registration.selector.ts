import { registrationFeature } from './registration.reducer';

export const {
  selectSuccess,
  selectChallengeId,
  selectError,
  selectLoading,
  selectRegistrationState,
  selectStep,
  selectToken,
} = registrationFeature;
