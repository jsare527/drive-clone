import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  baseUrl = "/api/auth"
  private jwtHelper = new JwtHelperService();

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string) {
    const userDto = { username, password };
  }

  isAuthed(): boolean {
    const token = sessionStorage.getItem('auth_token');

    if (token && !this.jwtHelper.isTokenExpired(token)) {
      return true;
    }

    sessionStorage.removeItem('auth_token');
    return false;
  }
}
