import {Routes} from '@angular/router';
import {HomePage} from "./pages/home/home-page.component";
import {CompanyViewComponent} from "./pages/company-view/company-view.component";
import {WelcomeComponent} from "./pages/home/welcome/welcome.component";
import {SearchResultComponent} from "./pages/home/search-result/search-result.component";
import {CompanyManagementPageComponent} from "./pages/company-management-page/company-management-page.component";
import {
    CompanyManagementHomeComponent
} from "./pages/company-management-page/company-management-home/company-management-home.component";
import {
    CompanyManagementEmployeeComponent
} from "./pages/company-management-page/company-management-employee/company-management-employee.component";
import {
    CompanyManagementOfferComponent
} from "./pages/company-management-page/company-management-offer/company-management-offer.component";
import {
    CompanyManagementAppointmentsComponent
} from "./pages/company-management-page/company-management-appointments/company-management-appointments.component";
import {MessagesPageComponent} from "./pages/messages-page/messages-page.component";
import {UserAppointmentsPageComponent} from "./pages/user-appointments-page/user-appointments-page.component";
import {PageNotFoundComponent} from "./pages/page-not-found/page-not-found.component";
import {AuthGuard} from "./auth.guard";
import {NavbarLayoutComponent} from "./pages/navbar-layout/navbar-layout.component";
import {AdminPanelComponent} from "./pages/admin-panel/admin-panel.component";
import {COMPANY_ROLE, Role} from "./model/http/auth.model";
import {
    CompanyManagementBookingsCalendarComponent
} from "./pages/company-management-page/company-management-bookings-calendar/company-management-bookings-calendar.component";
import {CompanyRoleGuard} from "./company-role.guard";
import {ForbiddenPageComponent} from "./pages/forbidden-page/forbidden-page.component";
import {ServerErrorPageComponent} from "./pages/server-error-page/server-error-page.component";

export const routes: Routes = [
    {path: '', redirectTo: "guest", pathMatch: 'full'},
    {
        path: 'guest',
        component: NavbarLayoutComponent,
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage ,
            children: [
                { path: '', redirectTo: "welcome", pathMatch: 'full'},
                { path: 'welcome', component: WelcomeComponent},
                { path: 'search', component: SearchResultComponent},
                { path: '**', component: PageNotFoundComponent },
            ]},
            { path: 'company/:id', component: CompanyViewComponent },
        ] },
    {
        path: 'app',
        component: NavbarLayoutComponent,
        canActivate: [AuthGuard],
        data: {roles: [Role.ROLE_ADMIN, Role.ROLE_USER]},
        children: [
            { path: '', redirectTo: "home", pathMatch: 'full' },
            { path: 'home', component: HomePage,
                children: [
                    { path: '', redirectTo: "welcome", pathMatch: 'full'},
                    { path: 'welcome', component: WelcomeComponent},
                    { path: 'search', component: SearchResultComponent},
                    { path: '**', component: PageNotFoundComponent },
                ]},
            { path: 'company/:id', component: CompanyViewComponent},
            {
                path: 'company-management',
                component: CompanyManagementPageComponent,
                canActivateChild: [CompanyRoleGuard],
                children: [
                    { path: '', redirectTo: "home", pathMatch: 'full'},
                    { path: 'home', component: CompanyManagementHomeComponent},
                    { path: 'employee', component: CompanyManagementEmployeeComponent,
                        data: {companyRoles: [COMPANY_ROLE.ROLE_OWNER, COMPANY_ROLE.ROLE_MANAGER]}
                    },
                    { path: 'offers', component: CompanyManagementOfferComponent,
                        data: {companyRoles: [COMPANY_ROLE.ROLE_OWNER, COMPANY_ROLE.ROLE_MANAGER]}
                    },
                    { path: 'appointments', component: CompanyManagementAppointmentsComponent},
                    { path: 'calendar', component: CompanyManagementBookingsCalendarComponent},
                    {path: 'forbidden', component: ForbiddenPageComponent},
                    { path: '**', component: PageNotFoundComponent },
                ]},
            { path: 'messages', component: MessagesPageComponent},
            { path: 'appointments', component: UserAppointmentsPageComponent},
            { path: '**', component: PageNotFoundComponent }
        ]
    },
    {
      path: 'admin',
      component: NavbarLayoutComponent,
      canActivate: [AuthGuard],
      data: {roles: [Role.ROLE_ADMIN]},
      children: [
          { path: '', redirectTo: "panel", pathMatch: 'full' },
          { path: 'panel', component: AdminPanelComponent,
          children: [
              { path: '**', component: PageNotFoundComponent },
          ]}
      ]
    },
    { path:'server-error', component: ServerErrorPageComponent },
    { path: '**', component: PageNotFoundComponent },
];
