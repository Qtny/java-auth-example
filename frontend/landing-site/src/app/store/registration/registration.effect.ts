import { inject, Injectable } from '@angular/core';
import { AuthService } from '../../auth/auth-service';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  registerNewUserAction,
  registerNewUserFailure,
  registerNewUserSuccess,
  verifyNewUserAction,
  verifyNewUserFailure,
  verifyNewUserSuccess,
} from './registration.action';
import { catchError, map, of, switchMap, withLatestFrom } from 'rxjs';
import { Store } from '@ngrx/store';
import { RegistrationState } from './registration.reducer';
import { selectChallengeId, selectToken } from './registration.selector';
import { INewUserApiResponse, IVerifyNewUserApiResponse } from '../../../models/auth.api';

@Injectable()
export class RegistrationEffect {
  private readonly authService = inject(AuthService);
  action$ = inject(Actions);
  store = inject(Store<RegistrationState>);

  registerNewUser$ = createEffect(() =>
    this.action$.pipe(
      ofType(registerNewUserAction),
      switchMap(({ payload }) =>
        this.authService.registerNewUser(payload).pipe(
          map((response: ApiResponse<INewUserApiResponse>) => {
            if (response.success) {
              return registerNewUserSuccess({
                success: true,
                data: response.data!,
              });
            }

            return registerNewUserFailure({
              success: false,
              errorMessage:
                response.error?.message || 'Failed to create a new user',
            });
          }),
          catchError((response) =>
            of(
              registerNewUserFailure({
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

  verifyRegisterNewUser$ = createEffect(() =>
    this.action$.pipe(
      ofType(verifyNewUserAction),
      withLatestFrom(
        this.store.select(selectToken),
        this.store.select(selectChallengeId)
      ),
      switchMap(([{ code }, token, challengeId]) => {
        if (!token || !challengeId) {
          return of(
            verifyNewUserFailure({
              success: false,
              errorMessage: 'Token or Challenge ID is missing',
            })
          );
        }

        return this.authService
          .verifyRegisterNewuser(code, token, challengeId)
          .pipe(
            map((response: ApiResponse<IVerifyNewUserApiResponse>) => {
              if (
                !response.success &&
                response.data?.code === 'MFA_NOT_ENABLED'
              ) {
                return verifyNewUserSuccess({
                  success: false,
                  data: response.data!,
                });
              }

              return verifyNewUserFailure({
                success: false,
                errorMessage:
                  response.error?.message || 'Failed to create a new user',
              });
            }),
            catchError((response) => {
              return of(
                verifyNewUserFailure({
                  success: false,
                  errorMessage:
                    response.error.error.message || 'Network or server error',
                })
              );
            })
          );
      })
    )
  );
}
