import { Routes } from '@angular/router';
import { HomePageComponent } from './root/home-page-component/home-page-component';
import { LoginPageComponent } from './auth/login/login-page-component/login-page-component';
import { RegisterPageComponent } from './auth/register/register-page-component/register-page-component';
import { RegisterOtpPageComponent } from './auth/register/register-otp-page-component/register-otp-page-component';
import { MfaNotEnabledPageComponent } from './auth/login/mfa-not-enabled-page-component/mfa-not-enabled-page-component';
import { VerifyMfaPageComponent } from './auth/login/verify-mfa-page-component/verify-mfa-page-component';

export const routes: Routes = [
    {
        path: "",
        pathMatch: "full",
        redirectTo: "/signin/enable-mfa"
    },
    {
        path: "home",
        component: HomePageComponent,
    },
    // {
    //     path: "auth",
    //     loadChildren: () => import("./auth/auth.routes").then(m => m.authRoutes)
    // }
    {
        path: "signin",
        component: LoginPageComponent,
    },
    {
        path: "signup",
        component: RegisterPageComponent,
    },
    {
        path: "signup/otp",
        component: RegisterOtpPageComponent,
    },
    {
        path: "signin/enable-mfa",
        component: MfaNotEnabledPageComponent,
    },
    {
        path: "signin/mfa/verify",
        component: VerifyMfaPageComponent,
    },
];
