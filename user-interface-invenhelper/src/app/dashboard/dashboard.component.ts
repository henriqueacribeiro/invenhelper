import { Component, OnInit } from '@angular/core';
import {ChartData, ChartEvent, ChartType} from "chart.js";
import {Product} from "../model/product";
import {ProductService} from "../service/product.service";
import {ProductStat} from "../model/product-stat";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  productsStats: ProductStat[] = [];
  //products: Product[] = [];

  x: number = 100;

  // Doughnut
  doughnutChartLabels: string[] = [ 'Inventory' ] ;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void{
    this.productService.getAllProductIDs().subscribe(productsIDs => {
      for(let productID of productsIDs){
        this.productService.getProductByID(productID).subscribe(product => {
          //this.products.push(product);
          this.createProductStat(product);
        })
      }
    })
  }

  createProductStat(prod: Product){
    this.productsStats.push(new ProductStat(prod.name, {
      labels: [prod.name, "Remaining Space"],
      datasets: [
        { data: [ prod.quantity, 1000-prod.quantity ] }
      ]
    }));
  }

}
