import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthenticationService } from './services/authentication.service';
import { catchError, map, Subscription, tap } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  subscriptions = new Subscription();

  constructor(private authService: AuthenticationService, private router: Router) {}

  ngOnInit(): void {
    if (this.authService.isAuthedLocal()) {
      this.subscriptions.add(
        this.authService.checkServerToken().subscribe(valid => {
          if (valid) {
            this.router.navigate(['/home']);
          } else {
            this.router.navigate(['/login']);
          }
        })
      );
    } else {
      this.router.navigate(['/login']);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  
  title = 'ui';
}
