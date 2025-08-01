import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-register-page-component',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    ReactiveFormsModule,
    RouterModule,
  ],
  templateUrl: './register-page-component.html',
})
export class RegisterPageComponent {
  passwordIndicator = signal(true);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly registerUserForm = this.formBuilder.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', Validators.required],
    password: ['', Validators.required],
    street1: ['', Validators.required],
    street2: ['', Validators.required],
    postalCode: ['', Validators.required],
    city: ['', Validators.required],
    state: ['', Validators.required],
    country: ['', Validators.required],
  });

  togglePassword(event: MouseEvent) {
    this.passwordIndicator.set(!this.passwordIndicator());
    event.stopPropagation();
  }

  submitRegister() {
    const newUser: INewUserForm = {
      firstName: this.registerUserForm.value.firstName!,
      lastName: this.registerUserForm.value.lastName!,
      email: this.registerUserForm.value.email!,
      password: this.registerUserForm.value.password!,
      address: {
        street1: this.registerUserForm.value.street1!,
        street2: this.registerUserForm.value.street2!,
        postalCode: this.registerUserForm.value.postalCode!,
        city: this.registerUserForm.value.city!,
        state: this.registerUserForm.value.state!,
        country: this.registerUserForm.value.country!,
      },
    };

    console.log('this is the form');
    console.log(newUser);
  }
}

interface INewUserForm {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: IUserAddress;
}

interface IUserAddress {
  street1: string;
  street2: string;
  postalCode: string;
  city: string;
  state: string;
  country: string;
}
