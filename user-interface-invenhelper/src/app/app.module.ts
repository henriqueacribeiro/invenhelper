import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import {NgChartsModule} from "ng2-charts";
import { ProductStatisticsComponent } from './product-statistics/product-statistics.component';
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import { ProductListComponent } from './product-list/product-list.component';
import {HttpClientModule} from "@angular/common/http";
import { HomeComponent } from './home/home.component';
import { ProductDetailsComponent } from './product-details/product-details.component';
import { ProductHomeComponent } from './product-home/product-home.component';
import { AlertComponent } from './alert/alert.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    ProductStatisticsComponent,
    ProductListComponent,
    HomeComponent,
    ProductDetailsComponent,
    ProductHomeComponent,
    AlertComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgChartsModule,
    FontAwesomeModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
