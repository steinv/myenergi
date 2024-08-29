import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { map, take } from 'rxjs';
import {MyenergiService, HistoryEntity} from './myenergi.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  private now = new Date();
  public maximumDate = new Date(Date.now() - 86400000);
  public minimumDate = new Date(2021, 0, 1);
  public dateRange = new FormGroup({
    start: new FormControl(this.now),
    end: new FormControl(this.now)
  });

  public constructor(
    private readonly datePipe: DatePipe,
    private readonly service: MyenergiService
  ) { }

  public ngOnInit(): void {
    this.dateRange.controls['start'].value;
  }

  public today(): void {
    this.dateRange.patchValue({ start: this.now, end: this.now });
  }

  public download(): void {
    this.service.latestData.pipe(
      take(1),
      map(data => this.mapDataToCsv(data))
    ).subscribe((data) => {
      const blob = new Blob([data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.href = url;
      a.download = `myenergi-${this.datePipe.transform(this.dateRange.controls['start'].value, 'yyyy.MM.dd')}-${this.datePipe.transform(this.dateRange.controls['end'].value, 'yyyy.MM.dd')}.csv`;
      a.click();
      setTimeout(() => {
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      }, 0)
    });
  }

  private mapDataToCsv(data: HistoryEntity[]): string {
    const csvData = data.map(row => Object.values({
      date: this.datePipe.transform(row.date, 'dd/MM/yyyy'),
      serial: row.serial,
      generated: `=(${row.generated} / 3600000)`,
      imported: `=(${row.imported} / 3600000)`,
      exported: `=(${row.exported} / 3600000)`,
      charged: `=(${row.charged} / 3600000)`,
      consumed: `=(${row.consumed} / 3600000)`,
    }).join(',')).join('\n');

    return [
      // header
      'date,Zappi serial,generated kWh,imported kWh,exported kWh,charged kWh,consumed kWh',
      // data
      csvData,
      //empty line
      ,
      // sum of total charged in kWh rounded with 2 decimals
      'Total charged:,"=ROUND(SUM(F2:F999);2)"'
    ].join('\n');
  }
}
