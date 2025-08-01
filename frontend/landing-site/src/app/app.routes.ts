import { Routes } from '@angular/router';
import { HomePageComponent } from './root/home-page-component/home-page-component';
import { LoginPageComponent } from './auth/login/login-page-component/login-page-component';
import { RegisterPageComponent } from './auth/register/register-page-component/register-page-component';
import { RegisterOtpPageComponent } from './auth/register/register-otp-page-component/register-otp-page-component';

export const routes: Routes = [
    // {
    //     path: "",
    //     pathMatch: "full",
    //     redirectTo: "/home"
    // },
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
];
