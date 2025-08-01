import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OtpInputComponent } from '../../../shared/otp-input-component/otp-input-component';

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

  handleSubmitOtp(code: string) {
    this.isSubmit.set(true);
    console.log('your code is => ', code);
  }
}
