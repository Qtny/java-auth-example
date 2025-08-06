import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import {
  IInitiateEmailMfaResponse,
  ILoginPayload,
  ILoginResponse,
  INewUserApiPayload,
  INewUserApiResponse,
  IVerifyLoginByEmailPayload,
  IVerifyLoginByTotpPayload,
  IVerifyLoginResponse,
  IVerifyNewUserApiPayload,
  IVerifyNewUserApiResponse,
} from '../../models/auth.api';
import { EMfaChallengeType } from '../../models/mfa.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private static BASE_URL = 'http://localhost:8083/api/v1/auth';
  private http = inject(HttpClient);

  registerNewUser(payload: INewUserApiPayload) {
    return this.http.post<ApiResponse<INewUserApiResponse>>(
      `${AuthService.BASE_URL}/register`,
      payload
    );
  }

  verifyRegisterNewuser(code: string, token: string, challengeId: string) {
    const payload: IVerifyNewUserApiPayload = {
      code,
      challengeId,
    };
    return this.http.post<ApiResponse<IVerifyNewUserApiResponse>>(
      `${AuthService.BASE_URL}/register/verify`,
      payload,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  login(email: string, password: string) {
    const payload: ILoginPayload = { email, password };
    return this.http.post<ApiResponse<ILoginResponse>>(
      `${AuthService.BASE_URL}/login`,
      payload
    );
  }

  initiateEmailMfa(token: string) {
    return this.http.post<ApiResponse<IInitiateEmailMfaResponse>>(
      `${AuthService.BASE_URL}/mfa/email/initiate`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  initiateTotpMfa(token: string) {
    return this.http.post<ApiResponse<null>>(
      `${AuthService.BASE_URL}/mfa/totp/initiate`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  verifyLoginByEmail(code: string, token: string, challengeId: string) {
    const payload: IVerifyLoginByEmailPayload = {
      code,
      challengeId,
    };
    console.log('[AUTH SERVICE] :: payload is ', payload);
    console.log('[AUTH SERVICE] :: token is ', token);
    return this.http.post<ApiResponse<IVerifyLoginResponse>>(
      `${AuthService.BASE_URL}/login/email/verify`,
      payload,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  verifyLoginByTotp(code: string, token: string) {
    const payload: IVerifyLoginByTotpPayload = {
      code,
    };
    console.log('[AUTH SERVICE] :: payload is ', payload);
    console.log('[AUTH SERVICE] :: token is ', token);
    return this.http.post<ApiResponse<IVerifyLoginResponse>>(
      `${AuthService.BASE_URL}/login/totp/verify`,
      payload,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }
}
