import { Component, inject, signal } from '@angular/core';
import { OtpInputComponent } from '../../../shared/otp-input-component/otp-input-component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-verify-mfa-page-component',
  imports: [OtpInputComponent],
  templateUrl: './verify-mfa-page-component.html',
})
export class VerifyMfaPageComponent {
  constructor(router: Router) {
    const navigation = router.getCurrentNavigation();
    const state = navigation?.extras.state;
    this.mfaMethod = state!['type'];
  }

  mfaMethod: EMfaChallengeType;
  private readonly router = inject(Router);

  isSubmit = signal(false);
  handleSubmitOtp(code: string) {
    this.isSubmit.set(true);
    console.log('logging in');
    setTimeout(() => {
      this.router.navigate(['/home']);
    }, 2000);
  }
}

enum EMfaChallengeType {
  EMAIL = 'email',
  SMS = 'sms',
  AUTHENTICATOR = 'totp',
  BIOEMTRIC = 'biometric',
}
