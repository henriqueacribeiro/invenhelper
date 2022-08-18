import {Component, Input, OnInit} from '@angular/core';
import {ProductService} from "../service/product.service";
import {Product} from "../model/product";
import {ProductStat} from "../model/product-stat";

@Component({
  selector: 'app-product-home',
  templateUrl: './product-home.component.html',
  styleUrls: ['./product-home.component.scss']
})
export class ProductHomeComponent implements OnInit {

  @Input() prod: Product = new Product("", "", "", 0);

  products: Product[] = [];

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void{
    this.productService.getAllProductIDs().subscribe(productsIDs => {
      for(let productID of productsIDs){
        this.productService.getProductByID(productID).subscribe(product => {
          this.products.push(product);
        })
      }
    });
  }

  refreshProd(prod: Product){
    this.prod = prod;
  }

}
