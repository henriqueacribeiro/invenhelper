import { Injectable } from '@angular/core';
import {Product} from "../model/product";
import {catchError, Observable, of} from "rxjs";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private httpClient: HttpClient) { }

  httpHeaders =  new HttpHeaders({'Content-Type': 'application/json'});

  getAllProductIDs(): Observable<string[]> {
    return this.httpClient.get<string[]>(environment.backend.host + environment.backend.getProducts, {
      headers: this.httpHeaders
    });
  }

  getProductByID(id: string): Observable<Product> {
    let params = new HttpParams().set("identifier", id);
    return this.httpClient.get<Product>(environment.backend.host + environment.backend.getProductByID, {
      params: params,
      headers: this.httpHeaders
    });
  }

  increaseQuantity(id: string, qtd: number): Observable<any> {
    let params = new HttpParams().set("identifier", id).set("quantity", qtd);
    return this.httpClient.put<any>(environment.backend.host + environment.backend.increaseQuantity, {}, {
      params: params,
      headers: this.httpHeaders
    });
  }

  decreaseQuantity(id: string, qtd: number): Observable<any> {
    let params = new HttpParams().set("identifier", id).set("quantity", qtd);
    return this.httpClient.put<any>(environment.backend.host + environment.backend.decreaseQuantity, {}, {
      params: params,
      headers: this.httpHeaders
    });
  }
}
