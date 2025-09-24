import {Routes} from '@angular/router';
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {StartPage} from "./pages/dashboard/start/start-page.component";

export const routes: Routes = [
    {  path: '', redirectTo: 'start', pathMatch: 'full' },
        {path: "start", component: StartPage },
        // TODO Pages for guests
    {
        path: 'app',
        component: DashboardComponent,
        children: [
            // TODO Logged user component
        ]
    }
];
