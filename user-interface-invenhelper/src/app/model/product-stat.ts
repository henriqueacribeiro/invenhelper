import {ChartData} from "chart.js";

export class ProductStat {

  productName: string[];
  productData: ChartData<'doughnut'>;

  constructor(name: string, data: ChartData<'doughnut'>) {
    this.productName = [name, "Remaining space"];
    this.productData = data;
  }
}
