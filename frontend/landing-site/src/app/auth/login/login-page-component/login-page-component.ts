import {
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewEncapsulation,
} from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { LoginState } from '../../../store/login/login.reducer';
import { loginAction } from '../../../store/login/login.action';
import { distinctUntilChanged, filter, Subject, takeUntil } from 'rxjs';
import {
  selectError,
  selectIsMfaEnbaled,
  selectLoading,
} from '../../../store/login/login.selector';
import { CommonModule } from '@angular/common';
import { openErrorDialogAction } from '../../../store/global/global.action';

@Component({
  selector: 'app-login-page-component',
  imports: [
    RouterModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    CommonModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './login-page-component.html',
  styleUrl: './login-page-component.css',
  encapsulation: ViewEncapsulation.None,
})
export class LoginPageComponent implements OnInit, OnDestroy {
  passwordIndicator = signal(true);
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly destroy$ = new Subject<void>();
  private readonly store = inject(Store<LoginState>);
  readonly loading$ = this.store.select(selectLoading);

  protected readonly loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  ngOnInit(): void {
    this.store
      .select(selectIsMfaEnbaled)
      .pipe(takeUntil(this.destroy$))
      .subscribe((val) => {
        if (val == null) return;
        if (val) {
          return this.router.navigate(['/signin/mfa/verify']);
        }

        return this.router.navigate(['/signin/enable-mfa']);
      });
    // error handling
    this.store
      .select(selectError)
      .pipe(takeUntil(this.destroy$))
      .subscribe((error) => {
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
        if (val) {
          this.loginForm.disable();
        } else {
          this.loginForm.enable();
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

  submitLogin() {
    const email = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;

    this.store.dispatch(loginAction({ email, password }));
  }
}

interface ILoginUser {
  email: string;
  password: string;
}
