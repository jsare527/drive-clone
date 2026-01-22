import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { userDto, userFormDto } from '../../models/userDto';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnDestroy {
  loginForm: FormGroup<userFormDto>;
  subscriptions = new Subscription();
  error = '';

  constructor(private fb: FormBuilder, private readonly authService: AuthenticationService, private readonly router: Router) {
    this.loginForm = new FormGroup<userFormDto>({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    })
  }


  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onSubmit() {
    this.error = '';
    const username = this.loginForm.controls.username.value!;
    const password = this.loginForm.controls.password.value!;

    localStorage.removeItem('auth_token');
    this.subscriptions.add(
      this.authService.login(username, password).subscribe(success => {
        if (!success) this.error = 'Invalid credentials';
      })
    );
  }

}
