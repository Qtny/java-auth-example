import {
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewEncapsulation,
} from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../store/app.state';
import {
  registerNewUserAction,
  resetRegistrationState,
} from '../../../store/registration/registration.action';
import { distinctUntilChanged, filter, Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { openErrorDialogAction } from '../../../store/global/global.action';
import { RegistrationState } from '../../../store/registration/registration.reducer';
import { selectError, selectLoading, selectToken } from '../../../store/registration/registration.selector';

@Component({
  selector: 'app-register-page-component',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    ReactiveFormsModule,
    RouterModule,
    MatProgressSpinnerModule,
    CommonModule,
  ],
  templateUrl: './register-page-component.html',
  styleUrl: './register-page-component.css',
  encapsulation: ViewEncapsulation.None,
})
export class RegisterPageComponent implements OnInit, OnDestroy {
  private readonly router = inject(Router);
  passwordIndicator = signal(true);
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store<RegistrationState>);

  private destroy$ = new Subject<void>();
  protected readonly loading$ = this.store.select(selectLoading);

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

  ngOnInit(): void {
    // reset the form
    this.store.dispatch(resetRegistrationState());
    // navigate on success
    this.store
      .pipe(
        select(selectToken),
        distinctUntilChanged((token) => token != null),
        filter((token) => !!token),
        takeUntil(this.destroy$)
      )
      .subscribe((_) => {
        this.router.navigate(['/signup/otp']);
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
    // loading handling
    this.loading$.pipe(takeUntil(this.destroy$)).subscribe((val) => {
      if (val) {
        this.registerUserForm.disable();
      } else {
        this.registerUserForm.enable();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

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

    this.store.dispatch(registerNewUserAction({ payload: newUser }));
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
