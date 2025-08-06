import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { OtpInputComponent } from '../../../shared/otp-input-component/otp-input-component';
import { Router } from '@angular/router';
import { EMfaChallengeType } from '../../../../models/mfa.model';
import { Store } from '@ngrx/store';
import { LoginState } from '../../../store/login/login.reducer';
import {
  selectError,
  selectLoading,
  selectMfaType,
  selectToken,
} from '../../../store/login/login.selector';
import { CommonModule } from '@angular/common';
import {
  verifyLoginByEmailAction,
  verifyLoginByTotpAction,
} from '../../../store/login/login.action';
import { distinctUntilChanged, filter, Subject, takeUntil, tap } from 'rxjs';
import { openErrorDialogAction } from '../../../store/global/global.action';

@Component({
  selector: 'app-verify-mfa-page-component',
  imports: [OtpInputComponent, CommonModule],
  templateUrl: './verify-mfa-page-component.html',
})
export class VerifyMfaPageComponent implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  private readonly store = inject(Store<LoginState>);
  private readonly destroy$ = new Subject<void>();
  readonly mfaMethod$ = this.store.select(selectMfaType);
  readonly isSubmit = signal(false);

  ngOnInit(): void {
    // nagivation
    this.store
      .select(selectToken)
      .pipe(
        distinctUntilChanged(),
        filter((token) => !!token),
        takeUntil(this.destroy$)
      )
      .subscribe((token) => {
        if (token) {
          this.router.navigate(['/home'], { replaceUrl: true });
        }
      });
    // error handling
    this.store.select(selectError).subscribe((error) => {
      if (error) {
        this.store.dispatch(
          openErrorDialogAction({
            title: "We can't proceed without your help",
            description: error,
          })
        );
        console.error('error during creation => ', error);
      }
    });
    // form handling
    this.store
      .select(selectLoading)
      .pipe(takeUntil(this.destroy$))
      .subscribe((val) => {
        this.isSubmit.set(val);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleSubmitOtp(code: string) {
    this.isSubmit.set(true);
    console.log('[Verify MFA page] :: submitted');

    this.mfaMethod$
      .pipe(
        takeUntil(this.destroy$),
        tap((type) => {
          console.log('[Verify MFA page] :: verifying in action');
          switch (type) {
            case EMfaChallengeType.EMAIL:
              console.log('[In TAP] :: method chosen is ', type);
              this.store.dispatch(verifyLoginByEmailAction({ code }));
              break;
            case EMfaChallengeType.TOTP:
              console.log('[In TAP] :: method chosen is ', type);
              this.store.dispatch(verifyLoginByTotpAction({ code }));
              break;
            default:
              console.error('No such method to mfa');
              this.isSubmit.set(false);
          }
        })
      )
      .subscribe();
  }
}
