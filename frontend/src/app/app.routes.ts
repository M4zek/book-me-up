import {Routes} from '@angular/router';
import {DashboardComponent} from "./pages/user-dashboard/dashboard.component";
import {HomePage} from "./pages/home/home-page.component";
import {GuestLayoutComponent} from "./pages/guest-layout/guest-layout.component";
import {CompanyViewComponent} from "./pages/company-view/company-view.component";
import {WelcomeComponent} from "./pages/home/welcome/welcome.component";
import {SearchResultComponent} from "./pages/home/search-result/search-result.component";

export const routes: Routes = [
    {path: '', redirectTo: "guest", pathMatch: 'full'},
    {
        path: 'guest',
        component: GuestLayoutComponent,
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage ,
            children: [
                { path: '', redirectTo: "welcome", pathMatch: 'full'},
                { path: 'welcome', component: WelcomeComponent},
                { path: 'search', component: SearchResultComponent}
            ]},
            { path: 'company', component: CompanyViewComponent }
        ]
    },
    {
        path: 'app',
        component: DashboardComponent,
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage,
                children: [
                    { path: '', redirectTo: "welcome", pathMatch: 'full'},
                    { path: 'welcome', component: WelcomeComponent},
                    { path: 'search', component: SearchResultComponent}
                ]},
            { path: 'company', component: CompanyViewComponent}
        ]
    }
];
