import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {LineChartComponent} from "../line-chart/line-chart.component";
import {BarChartComponent, ChartData} from "../bar-chart/bar-chart.component";

@Component({
  selector: 'app-stat-card',
  imports: [
    LineChartComponent,
    BarChartComponent
  ],
  templateUrl: './stat-card.component.html',
  styleUrl: './stat-card.component.css'
})
export class StatCardComponent implements OnChanges{

  @Input() public title: string = 'No Content';
  @Input() public icon: string = 'icons/pending_icon.svg';
  @Input() public stats_text: string = '';

  @Input() public chart_type: string = '';
  @Input() public chart_input: Record<string, number> = {}

  @Input() public content: string | number = 'No Content';
  @Input() public grown_up_progress: number | undefined = undefined;

  @Input() isLoading: boolean = true;


  chartData: ChartData[] = [];


  ngOnChanges(changes: SimpleChanges) {
    if(changes['chart_input']) {
      this.chartData = [];
      for (let key in this.chart_input) {
        const value = this.chart_input[key];
        this.chartData.push({
          value: value, name: key,
        })
      }
    }
  }


  ngOnInit(): void {
    setTimeout(() => {
      this.isLoading = false;
    }, 2000);
  }



}
