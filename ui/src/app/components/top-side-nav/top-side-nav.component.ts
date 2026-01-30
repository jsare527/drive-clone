import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { RouterModule, Router, RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-top-side-nav',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatSidenavModule,
    RouterOutlet,
    MatListModule,
    RouterLink,
    RouterModule
],
  templateUrl: './top-side-nav.component.html',
  styleUrl: './top-side-nav.component.scss'
})
export class TopSideNavComponent {

}
