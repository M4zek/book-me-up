import {Routes} from '@angular/router';
import {DashboardComponent} from "./pages/user-dashboard/dashboard.component";
import {HomePage} from "./pages/home/home-page.component";
import {GuestLayoutComponent} from "./pages/guest-layout/guest-layout.component";
import {CompanyViewComponent} from "./pages/company-view/company-view.component";

export const routes: Routes = [
    {path: '', redirectTo: "guest", pathMatch: 'full'},
    {
        path: 'guest',
        component: GuestLayoutComponent,
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage },
            { path: 'company', component: CompanyViewComponent }
        ]
    },
    {
        path: 'app',
        component: DashboardComponent,
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage },
            { path: 'company', component: CompanyViewComponent }
        ]
    }
];
