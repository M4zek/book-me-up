import {Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {getRandomRange} from "../../utils.functions";


export interface ChartData {
  name: string | number | Date;
  value: number;
}



@Component({
  selector: 'app-bar-chart',
  imports: [],
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.css'
})
export class BarChartComponent implements OnChanges {
  protected readonly getRandomRange = getRandomRange;

  protected object_id =
      'barChart' + Math.random().toString(36);

  private observer?: ResizeObserver;

  @ViewChild('barChart', {static:true})
  chart!: ElementRef<HTMLDivElement>;

  @Input()
  max_height:number = 50;

  @Input()
  showLabels:boolean = false;

  @Input()
  isLoading:boolean = true;

  @Input()
  data: ChartData[] = [];

  skeletonItems = Array(7).fill(0);

  height = 1;


  ngOnChanges(changes:SimpleChanges){
    if(changes['max_height']){
      this.height=this.max_height;
    }
  }

  ngAfterViewInit(){
    this.observer = new ResizeObserver(entries=>{
          const rect = entries[0].contentRect;
          this.height = rect.height;
        });

    this.observer.observe(
        this.chart.nativeElement
    );

  }

  ngOnDestroy(){
    this.observer?.disconnect();
  }


  get yAxisLabels():number[]{
    const max =
        Math.max(
            ...this.data.map(x=>x.value),
            1
        );
    return [
      Math.round(max),
      Math.round(max/2),
      0
    ];

  }

  protected calculateHeightPercent(d:ChartData):number{
    const max =
        Math.max(
            ...this.data.map(x=>x.value),
            1
        );

    const percent =
        (d.value/max)*100;

    return percent < 1
        ? 1
        : percent;

  }

  protected calculateHeight(d: ChartData): number {
    const max_value_in_data = Math.max(...this.data.map(item => item.value), 1);
    const value = (d.value / max_value_in_data) * this.max_height;

    return value < 2 ? 2 : value;
  }

}
