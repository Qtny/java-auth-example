import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { provideState, provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { registrationFeature } from './store/registration/registration.reducer';
import { RegistrationEffect } from './store/registration/registration.effect';
import { GlobalEffect } from './store/global/global.effect';
import { loginFeature } from './store/login/login.reducer';
import { LoginEffect } from './store/login/login.effect';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideStore(),
    provideEffects(),
    provideStore(),
    provideEffects(GlobalEffect),
    provideState(registrationFeature),
    provideEffects(RegistrationEffect),
    provideState(loginFeature),
    provideEffects(LoginEffect),
  ],
};
