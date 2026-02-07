import { Component, DestroyRef, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { userDto, userFormDto } from '../../models/userDto';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup<userFormDto>;
  destroyRef = inject(DestroyRef);
  isLoginMode: boolean = true;
  error = '';

  constructor(private readonly authService: AuthenticationService) {
    this.loginForm = new FormGroup<userFormDto>({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    })
  }

  onLogin() {
    const userDto: userDto = this.getFormData();

    localStorage.removeItem('auth_token');
    this.authService.login(userDto.username, userDto.password)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe(success => {
      if (!success) this.error = 'Invalid credentials';
    });
  }

  onRegister() {
    const userDto: userDto = this.getFormData();

    localStorage.removeItem('auth_token');
    this.authService.register(userDto.username, userDto.password)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe(success => {
      if (!success) this.error = 'Username already taken';
    });
  }

  switchModes() {
    this.isLoginMode = !this.isLoginMode;
  }

  getFormData(): userDto {
    this.error = '';
    return { username: this.loginForm.controls.username.value!, password: this.loginForm.controls.password.value! };
  }

}
