import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OtpInputComponent } from '../../../shared/otp-input-component/otp-input-component';
import { Router } from '@angular/router';

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
export class RegisterOtpPageComponent {
  protected readonly mockemail = 'email.address@testing.com';
  protected readonly isSubmit = signal(false);
  protected readonly router = inject(Router);

  handleSubmitOtp(code: string) {
    this.isSubmit.set(true);
    console.log('your code is => ', code);

    setTimeout(() => {
      this.router.navigate(["/signin/enable-mfa"]);
    }, 1000);
  }
}
