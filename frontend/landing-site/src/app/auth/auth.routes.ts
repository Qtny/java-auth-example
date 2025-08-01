import { Routes } from "@angular/router";
import { LoginPageComponent } from "./login/login-page-component/login-page-component";

export const authRoutes: Routes = [
    {
        path: "login",
        component: LoginPageComponent
    }
]