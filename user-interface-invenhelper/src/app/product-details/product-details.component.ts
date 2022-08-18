import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Product} from "../model/product";
import {ProductService} from "../service/product.service";

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss']
})
export class ProductDetailsComponent implements OnInit {

  @Input() product: Product = new Product("", "", "", 0);

  // @ts-ignore
  @ViewChild("qtdInput") qtdInput: ElementRef;

  showAlert: boolean = false;
  alertType: string = "";
  alertMessage: string = "";

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
  }

  increaseQuantity(quantity: number){
    this.productService.increaseQuantity(this.product.identifier, quantity).subscribe({
      next: res => {
        console.log();
        this.product.quantity = res.object.quantity;
        this.qtdInput.nativeElement.value = "";
      },
      error: res => {
        this.alertType = "danger";
        this.alertMessage = res.error.information;
        this.showAlert = true;
      }
    });
  }

  decreaseQuantity(quantity: number){
    this.productService.decreaseQuantity(this.product.identifier, quantity).subscribe({
      next: res => {
        console.log(res);
        this.product.quantity = res.object.quantity;
        this.qtdInput.nativeElement.value = "";
      },
      error: res => {
        this.alertType = "danger";
        this.alertMessage = res.error.information;
        this.showAlert = true;
      }
    })
  }

  hideAlert(bool: boolean){
    this.showAlert = false;
  }


}
