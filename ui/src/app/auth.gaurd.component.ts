import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthenticationService } from "./services/authentication.service";

export const authGaurd: CanActivateFn = (route, state) => {
    const authService = inject(AuthenticationService);
    const router = inject(Router);

    if (authService.isAuthed()) {
        return true;
    } else {
        router.navigate(['/login']);
        return false;
    }
}