import {Component, OnInit, signal} from '@angular/core';
import {BarChartComponent, ChartData} from "../../../components/bar-chart/bar-chart.component";
import {LineChartComponent} from "../../../components/line-chart/line-chart.component";
import {CompanyRankingComponent} from "../../../components/company-ranking/company-ranking.component";
import {TicketSummaryComponent} from "../../../components/ticket-summary/ticket-summary.component";
import {StatCardComponent} from "../../../components/stat-card/stat-card.component";
import {CompanyRankingItem, DashboardStatistics} from "../../../model/gui/admin.gui.model";
import {StatsService} from "../../../service/stats.service";
import {ToastService} from "../../../service/toast.service";
import {UserService} from "../../../service/user.service";
import {CompanyService} from "../../../service/company.service";
import {Page, Pagination} from "../../../model/search/search.model";

@Component({
  selector: 'app-admin-home',
    imports: [
        LineChartComponent,
        CompanyRankingComponent,
        TicketSummaryComponent,
        StatCardComponent,
        BarChartComponent
    ],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css'
})
export class AdminHomeComponent implements OnInit {

    stats = signal<DashboardStatistics | null>(null);
    isStatsLoading = signal(true);

    isPopularCompaniesLoading = signal(true);
    companies: Page<CompanyRankingItem> | null = null;

    isUserGrowthTrendLoading = signal(true);
    growthDataTrend: ChartData[] = [];

    isUserLoginTrendLoading = signal(true);
    loginDataTrend: ChartData[] = [];

    constructor(public statsService: StatsService,
                private userService: UserService,
                private companyService: CompanyService,
                private toast: ToastService,) {
    }

    ngOnInit(): void {
        this.loadStatistics();
        this.loadUserGrowthTrend();
        this.loadUserLoginTrend();
        this.loadPopularCompanies(null);
    }

    private loadStatistics(): void {
        this.isStatsLoading.set(true);
        this.statsService.getDashboardStats().subscribe({
            next: data => {
                this.stats.set(data.body);
                this.isStatsLoading.set(false);
            },
            error: error => {
                this.isStatsLoading.set(false);
                this.toast.show("An error occurred while loading the statistics.")
                console.error(error);
            }
        })
    }

    private loadUserGrowthTrend(): void {
        this.growthDataTrend = [];
        this.isUserGrowthTrendLoading.set(true)
        this.userService.getUsersGrowthTrend().subscribe({
            next: data => {
                data.body?.forEach((month) => {
                    this.growthDataTrend.push({
                        name: month.month,
                        value: month.count
                    })
                })
                this.isUserGrowthTrendLoading.set(false);
            }, error: error => {
                this.isUserGrowthTrendLoading.set(false);
                this.toast.show("An error occurred while loading user growth trends.")
                console.error(error);
            }
        })
    }

    private loadUserLoginTrend(): void {
        this.loginDataTrend = [];
        this.isUserLoginTrendLoading.set(true);
        this.userService.getUsersLoginTrend().subscribe({
            next: data => {
                data.body?.forEach((month) => {
                    this.loginDataTrend.push({
                        name: month.month,
                        value: month.count
                    })
                })
                this.isUserLoginTrendLoading.set(false);
            }, error: error => {
                this.isUserLoginTrendLoading.set(false);
                this.toast.show("An error occurred while loading user growth trends.", "error");
                console.error(error);
            }
        })
    }

    private loadPopularCompanies(page: Pagination | null): void {
        this.isPopularCompaniesLoading.set(true);
        this.companyService.getPopularCompanies(page).subscribe({
            next: data => {
                this.companies = data.body;
                this.isPopularCompaniesLoading.set(false);
            },
            error: error => {
                this.isPopularCompaniesLoading.set(false);
                this.toast.show("An error occurred while loading companies.", "error");
                console.error(error);
            }
        })
    }

    protected onCompaniesPaginatorChanged($event: Pagination) {
        this.loadPopularCompanies($event);
    }
}
