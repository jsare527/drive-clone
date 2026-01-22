import { Component } from '@angular/core';
import { TopSideNavComponent } from "../top-side-nav/top-side-nav.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [TopSideNavComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
