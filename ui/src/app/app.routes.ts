import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { authGaurd } from './auth.gaurd.component';
import { HomeComponent } from './components/home/home.component';
import { TopSideNavComponent } from './components/top-side-nav/top-side-nav.component';
import { TrashComponent } from './components/trash/trash.component';
import { MainComponent } from './components/main/main.component';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    {
        path: '',
        component: TopSideNavComponent,
        children: [
            { path: 'home', component: MainComponent, canActivate: [authGaurd] },
            { path: 'trash', component: TrashComponent, canActivate: [authGaurd]}
        ]
    },
    { path: '**', redirectTo: '/login' },
];
