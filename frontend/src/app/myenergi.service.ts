import {DatePipe} from '@angular/common';
import {Database, objectVal, listVal, ref, query, orderByChild, startAt, endAt} from '@angular/fire/database';
import {Inject, Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {take, tap} from 'rxjs/operators';
import {ZAPPI_SERIAL} from "./app.module";

export interface StatusCall {
  // TODO
}

export interface HistoryEntity {
  date: number;
  serial: string;
  generated: number;
  imported: number;
  exported: number;
  charged: number;
  consumed: number;
}

@Injectable()
export class MyenergiService {

  private latest$ = new ReplaySubject<HistoryEntity[]>(1);
  public latestData = this.latest$.asObservable();

  constructor(
    private readonly database: Database,
    private readonly _datePipe: DatePipe,
    @Inject(ZAPPI_SERIAL) private readonly zappiSerial: string
  ) {
  }

  public getHistoryOnDate(date: Date, serial?: string): Observable<HistoryEntity> {
    const zappiSerial = serial || this.zappiSerial;
    return objectVal<HistoryEntity>(
      ref(this.database, `/history/${zappiSerial}/${this._datePipe.transform(date, 'yyyy-MM-dd')}`)
    ).pipe(
      tap(data => this.latest$.next([data]))
    );
  }

  public getHistoryInRage(start: Date, end: Date, serial?: string): Observable<HistoryEntity[]> {
    const zappiSerial = serial || this.zappiSerial;

    return listVal<HistoryEntity>(
      query(
        ref(this.database, `/history/${zappiSerial}`),
        orderByChild('date'), startAt(start.getTime()), endAt(end.getTime())
      )
    ).pipe(
      tap(data => console.log(data)),
      tap(data => this.latest$.next(data))
    );
  }
}
