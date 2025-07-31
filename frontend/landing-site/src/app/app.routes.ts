import { Routes } from '@angular/router';
import { HomePageComponent } from './root/home-page-component/home-page-component';

export const routes: Routes = [
    {
        path: "",
        pathMatch: "full",
        redirectTo: "/home"
    },
    {
        path: "home",
        component: HomePageComponent,
    },
    {
        path: "auth",
        loadChildren: () => import("./auth/auth.routes").then(m => m.authRoutes)
    }
];
