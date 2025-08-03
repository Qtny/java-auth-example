import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-login-page-component',
  imports: [
    RouterModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './login-page-component.html',
})
export class LoginPageComponent {
  passwordIndicator = signal(true);
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);

  protected readonly loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  togglePassword(event: MouseEvent) {
    this.passwordIndicator.set(!this.passwordIndicator());
    event.stopPropagation();
  }

  submitLogin() {
    const loginUser: ILoginUser = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };

    console.log('this user is logging in');
    console.log(loginUser);

    this.router.navigate(["/signin/enable-mfa"])
  }
}

interface ILoginUser {
  email: string;
  password: string;
}
