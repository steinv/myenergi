import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { ChartType, ChartOptions, ChartDataset } from 'chart.js';
import { Observable, ReplaySubject } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { DayCall, MyenergiService } from '../myenergi.service';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  start?: Date | null;

  @Input()
  end?: Date | null;

  // replay subject with a cache of 1 element
  private updateSubject$ = new ReplaySubject<{start: Date, end: Date}>(1);

  private consumed: ChartDataset = {
    label: 'consumed',
    data: [],
    backgroundColor: '#ff6384',
    pointBackgroundColor: '#ff6384'
  };
  private evCharged: ChartDataset = {
    data: [],
    label: 'EV charged',
    backgroundColor: '#36A2EB',
    pointBackgroundColor: '#36A2EB'
  };         
  private solarPanels: ChartDataset = {
    data: [],
    label: 'solar-panels',
    backgroundColor: '#FFCE56',
    pointBackgroundColor: '#FFCE56'
  };
  private imported: ChartDataset =  {
    data: [],
    label: 'imported',
    backgroundColor: '#4BC0C0',
    pointBackgroundColor: '#4BC0C0'
  };
  private exported: ChartDataset = {
    data: [],
    label: 'exported',
    backgroundColor: '#E7E9EB',
    pointBackgroundColor: '#E7E9EB'
  };

  public chartOptions: ChartOptions = {
    responsive: true,
  };
  // TLabel
  public chartLabels: string[] = [];
  public chartType: ChartType = 'bar';
  public chartLegend = true;
  public chartPlugins = [];
  public chartDataset: ChartDataset[] = [this.consumed, this.evCharged, this.solarPanels, this.exported, this.imported];
  
  public constructor(
    private readonly _changeDetector: ChangeDetectorRef,
    private readonly _service: MyenergiService,
    private readonly _datePipe: DatePipe,
  ) { }

  public ngOnInit(): void {
    this.updateSubject$.pipe(
      switchMap(value => this.updateChart(value.start, value.end)),
      catchError((error, caught) => {
        console.error(error);
        return caught;
      })
    ).subscribe(
      () => this._changeDetector.markForCheck()
    );
  }


  public ngOnDestroy(): void {
    this.updateSubject$.complete();
  }

  public ngOnChanges(): void {
    if(this.start && this.end) {
      this.updateSubject$.next({start: this.start, end: this.end});
    }
  }

  private updateChart(start: Date, end: Date) {
      if(start.getTime() == end.getTime()) {
        this.chartType = 'bar';
        return this.getChartForDate(start);
      } else {
        this.chartType = 'line';
        return this.getChartForRange(start, end);
      }
  }

  private getChartForDate(date: Date): Observable<void> {
    return this._service.getHistoryOnDate(date).pipe(
      tap(() => this.resetData()),
      map(history => this.populateData([history]))
    );
  }

  private getChartForRange(start: Date, end: Date): Observable<void> {
    return this._service.getHistoryInRage(start, end).pipe(
      tap(() => this.resetData()),
      map(history => this.populateData(history.days))
    );
  }

  /** reset back to empty data sets */
  private resetData() {
    this.chartLabels = [];
    this.consumed.data = [];
    this.evCharged.data = [];
    this.solarPanels.data = [];
    this.imported.data = [];
    this.exported.data = [];
  }

  /** populate data sets with received information */
  private populateData(days: Array<DayCall>) {
    days.map(r => {
      this.chartLabels.push(this._datePipe.transform(new Date(r.date), 'dd/MM/yyyy')!);
      this.consumed.data?.push(r.consumed / 3600000);
      this.evCharged.data?.push(r.charged / 3600000);
      this.solarPanels.data?.push(r.generated / 3600000);
      this.imported.data?.push(r.imported / 3600000);
      this.exported.data?.push(r.exported / 3600000);
    });
  }

}
