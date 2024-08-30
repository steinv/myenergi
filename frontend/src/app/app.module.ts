import { DatePipe } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import {APP_INITIALIZER, InjectionToken, NgModule} from '@angular/core';
import { initializeApp, provideFirebaseApp } from "@angular/fire/app";
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { DateAdapter, MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgChartsModule } from 'ng2-charts';
import { AppComponent } from './app.component';
import { ChartComponent } from './chart/chart.component';
import { CustomDateAdapter } from './custom-date-adapter';
import { MyenergiService } from './myenergi.service';
import { environment } from "../environments/environment";
import {connectDatabaseEmulator, getDatabase, provideDatabase} from "@angular/fire/database";

export const ZAPPI_SERIAL = new InjectionToken<string>('ZAPPI_SERIAL');

@NgModule(
  {
    declarations: [
        AppComponent,
        ChartComponent,
    ],
    bootstrap: [AppComponent],
    imports: [BrowserModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatNativeDateModule,
        NgChartsModule], providers: [
        MyenergiService,
        provideFirebaseApp(() => initializeApp(environment.firebase)),
        provideDatabase(() => {
          const db = getDatabase();
          if (environment.emulator) {
            connectDatabaseEmulator(db, 'localhost', 9000);
          }
          return db;
        }),
        DatePipe,
        {
            provide: DateAdapter,
            useClass: CustomDateAdapter
        },
        {
            provide: MAT_DATE_LOCALE,
            useValue: 'en-UK',
        },
        {
            provide: ZAPPI_SERIAL, useValue: environment.zappi
        },
        provideHttpClient(withInterceptorsFromDi())
    ]
  })
export class AppModule { }
