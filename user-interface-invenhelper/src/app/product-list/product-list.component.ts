import { Component, OnInit } from '@angular/core';
import {ProductStat} from "../model/product-stat";
import {ProductService} from "../service/product.service";
import {Product} from "../model/product";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {

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
