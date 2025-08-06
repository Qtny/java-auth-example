import { EMfaChallengeType } from './mfa.model';

interface INewUserApiPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: IUserAddress;
}

interface INewUserApiResponse {
  token: string;
  challengeId: string;
}

interface IVerifyNewUserApiPayload {
  code: string;
  challengeId: string;
}

interface IVerifyNewUserApiResponse {
  token: string;
  code: string;
  message: string;
}

interface ILoginPayload {
  email: string;
  password: string;
}

interface ILoginResponse {
  token?: string;
  type?: EMfaChallengeType;
  challengeId?: string | null;
  code?: string;
  message?: string;
}

interface IInitiateEmailMfaResponse {
  challengeId: string;
}

interface IVerifyLoginByEmailPayload {
  code: string;
  challengeId: string;
}

interface IVerifyLoginByTotpPayload {
  code: string;
}

interface IVerifyLoginResponse {
  token: string;
}
