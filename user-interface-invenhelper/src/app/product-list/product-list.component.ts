import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ProductStat} from "../model/product-stat";
import {ProductService} from "../service/product.service";
import {Product} from "../model/product";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {

  @Input() products: Product[] = [];

  @Output() selectProductStat = new EventEmitter<ProductStat>();

  @Output() selectProductDet = new EventEmitter<Product>();

  constructor() { }

  ngOnInit(): void {
  }

  sendProduct(prod: Product): void{
    this.selectProductStat.emit(new ProductStat(prod.name, {
      labels: [prod.name, "Remaining Space"],
      datasets: [
        { data: [ prod.quantity, 1000-prod.quantity ] }
      ]
    }));
    this.selectProductDet.emit(prod);
  }

}
