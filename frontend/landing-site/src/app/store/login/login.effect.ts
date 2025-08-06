import { inject, Injectable } from '@angular/core';
import { AuthService } from '../../auth/auth-service';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { LoginState } from './login.reducer';
import { catchError, map, of, switchMap, withLatestFrom } from 'rxjs';
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
  verifyLoginByEmailAction,
  verifyLoginByEmailActionFailure,
  verifyLoginByEmailActionSuccess,
  verifyLoginByTotpAction,
  verifyLoginByTotpActionFailure,
  verifyLoginByTotpActionSuccess,
} from './login.action';
import {
  IInitiateEmailMfaResponse,
  ILoginResponse,
  IVerifyLoginResponse,
} from '../../../models/auth.api';
import {
  selectChallengeId,
  selectMfaToken,
  selectToken,
} from './login.selector';

Injectable();
export class LoginEffect {
  private readonly authService = inject(AuthService);
  action$ = inject(Actions);
  store = inject(Store<LoginState>);

  login$ = createEffect(() =>
    this.action$.pipe(
      ofType(loginAction),
      switchMap(({ email, password }) =>
        this.authService.login(email, password).pipe(
          map((response: ApiResponse<ILoginResponse>) => {
            if (response.success) {
              return loginActionSuccess({
                success: true,
                data: response.data!,
              });
            } else if (
              !response.success &&
              response.data?.code === 'MFA_NOT_ENABLED'
            ) {
              return loginActionSuccess({
                success: true,
                data: response.data!,
              });
            }

            return loginActionFailure({
              success: false,
              errorMessage:
                response.error?.message || 'Failed to create a new user',
            });
          }),
          catchError((response) =>
            of(
              loginActionFailure({
                success: false,
                errorMessage:
                  response.error.error.message || 'Network or server error',
              })
            )
          )
        )
      )
    )
  );

  initiateEmailMfa$ = createEffect(() =>
    this.action$.pipe(
      ofType(initiateEmailMfaAction),
      withLatestFrom(this.store.select(selectMfaToken)),
      switchMap(([{}, mfaToken]) => {
        if (!mfaToken) {
          return of(
            initiateEmailMfaActionFailure({
              success: false,
              errorMessage: 'Token or Challenge ID is missing',
            })
          );
        }

        return this.authService.initiateEmailMfa(mfaToken).pipe(
          map((response: ApiResponse<IInitiateEmailMfaResponse>) => {
            if (response.success) {
              return initiateEmailMfaActionSuccess({
                success: true,
                data: response.data!,
              });
            }

            return initiateEmailMfaActionFailure({
              success: false,
              errorMessage:
                response.error?.message || 'Failed to create email mfa challenge, Please try again later.',
            });
          }),
          catchError((response) =>
            of(
              initiateEmailMfaActionFailure({
                success: false,
                errorMessage:
                  response.error.error.message || 'Network or server error',
              })
            )
          )
        );
      })
    )
  );

  initiateTotpMfa$ = createEffect(() =>
    this.action$.pipe(
      ofType(initiateTotpMfaAction),
      withLatestFrom(
        this.store.select(selectMfaToken),
      ),
      switchMap(([{}, mfaToken]) => {
        if (!mfaToken) {
          return of(
            initiateTotpMfaActionFailure({
              success: false,
              errorMessage: 'Token or Challenge ID is missing',
            })
          );
        }
        return this.authService.initiateTotpMfa(mfaToken).pipe(
          map((response: ApiResponse<null>) => {
            if (response.success) {
              return initiateTotpMfaActionSuccess({
                success: true,
                data: response.data!,
              });
            }

            return initiateTotpMfaActionFailure({
              success: false,
              errorMessage:
                response.error?.message || 'Failed to create a new user',
            });
          }),
          catchError((response) =>
            of(
              initiateTotpMfaActionFailure({
                success: false,
                errorMessage:
                  response.error.error.message || 'Network or server error',
              })
            )
          )
        );
      })
    )
  );

  verifyEmailMfaLogin$ = createEffect(() =>
    this.action$.pipe(
      ofType(verifyLoginByEmailAction),
      withLatestFrom(
        this.store.select(selectMfaToken),
        this.store.select(selectChallengeId)
      ),
      switchMap(([{ code }, mfaToken, challengeId]) => {
        if (!mfaToken || !challengeId) {
          return of(
            verifyLoginByEmailActionFailure({
              success: false,
              errorMessage: 'Token or Challenge ID is missing',
            })
          );
        }
        return this.authService
          .verifyLoginByEmail(code, mfaToken, challengeId)
          .pipe(
            map((response: ApiResponse<IVerifyLoginResponse>) => {
              if (response.success) {
                return verifyLoginByEmailActionSuccess({
                  success: true,
                  data: response.data!,
                });
              }

              return verifyLoginByEmailActionFailure({
                success: false,
                errorMessage:
                  response.error?.message || 'Failed to create a new user',
              });
            }),
            catchError((response) =>
              of(
                verifyLoginByEmailActionFailure({
                  success: false,
                  errorMessage:
                    response.error.error.message || 'Network or server error',
                })
              )
            )
          );
      })
    )
  );

  verifyTotpMfaLogin$ = createEffect(() =>
    this.action$.pipe(
      ofType(verifyLoginByTotpAction),
      withLatestFrom(this.store.select(selectMfaToken)),
      switchMap(([{ code }, mfaToken]) => {
        if (!mfaToken) {
          return of(
            verifyLoginByTotpActionFailure({
              success: false,
              errorMessage: 'Token or Challenge ID is missing',
            })
          );
        }
        return this.authService.verifyLoginByTotp(code, mfaToken).pipe(
          map((response: ApiResponse<IVerifyLoginResponse>) => {
            if (response.success) {
              return verifyLoginByTotpActionSuccess({
                success: true,
                data: response.data!,
              });
            }

            return verifyLoginByTotpActionFailure({
              success: false,
              errorMessage:
                response.error?.message || 'Failed to create a new user',
            });
          }),
          catchError((response) =>
            of(
              verifyLoginByTotpActionFailure({
                success: false,
                errorMessage:
                  response.error.error.message || 'Network or server error',
              })
            )
          )
        );
      })
    )
  );
}
