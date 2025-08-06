import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OtpInputComponent } from '../../../shared/otp-input-component/otp-input-component';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { verifyNewUserAction } from '../../../store/registration/registration.action';
import { distinctUntilChanged, filter, Subject, takeUntil } from 'rxjs';
import {
  selectError,
  selectStep,
} from '../../../store/registration/registration.selector';
import { openErrorDialogAction } from '../../../store/global/global.action';
import { ERegistrationStep } from '../../../store/registration/registration.reducer';

@Component({
  selector: 'app-register-otp-page-component',
  imports: [
    MatInputModule,
    CommonModule,
    MatFormFieldModule,
    OtpInputComponent,
  ],
  templateUrl: './register-otp-page-component.html',
})
export class RegisterOtpPageComponent implements OnInit, OnDestroy {
  protected readonly mockemail = 'email.address@testing.com';
  protected readonly isSubmit = signal(false);
  protected readonly router = inject(Router);
  private readonly store = inject(Store);
  private readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    // error handling
    this.store
      .select(selectError)
      .pipe(takeUntil(this.destroy$))
      .subscribe((error) => {
        if (error) {
          this.store.dispatch(
            openErrorDialogAction({
              title: 'Your OTP has issue',
              description: error,
            })
          );
        }
      });
    // navigation
    this.store
      .select(selectStep)
      .pipe(
        filter((step) => step === ERegistrationStep.COMPLETE),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() =>
        this.router.navigate(['/signin/enable-mfa'], { replaceUrl: true })
      );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleSubmitOtp(code: string) {
    this.isSubmit.set(true);

    this.store.dispatch(verifyNewUserAction({ code }));
  }
}
