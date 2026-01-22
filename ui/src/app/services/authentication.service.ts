import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { catchError, map, Observable, of } from 'rxjs';
import { userDto } from '../models/userDto';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  baseUrl = "/api/auth"
  private jwtHelper = new JwtHelperService();

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(username: string, password: string) {
    const userDto: userDto = { username, password };
    return this.http.post(`${this.baseUrl}/login`, userDto).pipe(
      map((response: any) => {
        localStorage.setItem('auth_token', response.token);
        this.router.navigate(['home']);
        return true;
      }),
      catchError(() => of(false))
    );
  }

  logout() {
    localStorage.removeItem('auth_token');
    this.router.navigate(['/login']);
  }

  isAuthedLocal(): boolean {
    const token = localStorage.getItem('auth_token');

    if (token && !this.jwtHelper.isTokenExpired(token)) {
      return true;
    }

    localStorage.removeItem('auth_token');
    return false;
  }

  checkServerToken(): Observable<boolean> {
    const token = localStorage.getItem('auth_token');
    if (!token) return of(false);

    return this.http.post(`${this.baseUrl}/tokenValid`, token).pipe(
      map(() => true),
      catchError(() => of(false))
    )
  }
}
