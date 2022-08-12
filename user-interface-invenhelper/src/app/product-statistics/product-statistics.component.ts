import {Component, Input, OnInit} from '@angular/core';
import {ChartData, ChartEvent, ChartType} from "chart.js";
import {Product} from "../model/product";
import {ProductStat} from "../model/product-stat";

@Component({
  selector: 'app-product-statistics',
  templateUrl: './product-statistics.component.html',
  styleUrls: ['./product-statistics.component.scss']
})
export class ProductStatisticsComponent implements OnInit {

  @Input() product: ProductStat = new ProductStat("", { labels: [], datasets: [] });

  doughnutChartType: ChartType = 'doughnut';

  constructor() { }

  ngOnInit(): void {
  }

  // events
  public chartClicked({ event, active }: { event: ChartEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public chartHovered({ event, active }: { event: ChartEvent, active: {}[] }): void {
    console.log(event, active);
  }

}
