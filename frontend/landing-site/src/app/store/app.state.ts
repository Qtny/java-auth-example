import { LoginState } from "./login/login.reducer";
import { RegistrationState } from "./registration/registration.reducer";

export interface AppState {
    registration: RegistrationState,
    login: LoginState
}