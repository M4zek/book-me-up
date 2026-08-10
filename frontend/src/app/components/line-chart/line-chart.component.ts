import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {ChartData} from "../bar-chart/bar-chart.component";

@Component({
  selector: 'app-line-chart',
  imports: [],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css'
})
export class LineChartComponent implements OnChanges, OnInit {
  private observer?: ResizeObserver;

  @Input() data: ChartData[] = [];

  @Input() showLabels = false;
  @Input() whiteGradient = true;
  @Input() isLoading = true;

  @ViewChild('chart', { static: true })
  chart!: ElementRef<HTMLDivElement>;

  width = 800;
  height = 600;

  gradient_id = 'areaGradient-' + Math.random().toString(36);

  constructor(private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.isLoading = false;
    }, 2500);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['showLabels']) {
      setTimeout(() => this.cd.detectChanges());
    }
  }

  ngAfterViewInit(): void {
    this.observer = new ResizeObserver(entries => {
      const rect = entries[0].contentRect;
      this.width = rect.width;
      this.height = rect.height;
    });

    this.observer.observe(this.chart.nativeElement);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  get maxValue(): number {
    return Math.max(...this.data.map(x => x.value), 1);
  }

  get minValue(): number {
    return 0;
  }

  protected getPoints() {

    const max = this.maxValue;

    return this.data.map((item, index) => ({

      x:
          (index / Math.max(this.data.length - 1, 1)) * this.width,

      y:
          this.height -
          (item.value / max) * this.height

    }));

  }

  protected getLinePath() {

    const points = this.getPoints();

    if (!points.length) {
      return '';
    }

    let path = `M ${points[0].x},${points[0].y}`;

    for (let i = 1; i < points.length; i++) {

      const prev = points[i - 1];
      const current = points[i];

      const middleX = (prev.x + current.x) / 2;

      path += `
        Q ${prev.x},${prev.y}
        ${middleX},${(prev.y + current.y) / 2}
      `;
    }

    path += ` T ${points.at(-1)!.x},${points.at(-1)!.y}`;

    return path;
  }

  protected getAreaPath() {
    const points = this.getPoints();

    if (!points.length) {
      return '';
    }

    return `
      ${this.getLinePath()}
      L ${points.at(-1)!.x} ${this.height}
      L ${points[0].x} ${this.height}
      Z
    `;
  }

  protected getYLabels(): number[] {
    const max = this.maxValue;
    return [
      max,
      Math.round(max / 2),
      0
    ];
  }

  protected getXPercent(index: number): number {
    if (this.data.length <= 1) {
      return 0;
    }
    return (index / (this.data.length - 1)) * 100;

  }

  protected getYPercent(value: number): number {
    return (value / this.maxValue) * 100;
  }
}
