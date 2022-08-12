import { Injectable } from '@angular/core';
import {Product} from "../model/product";
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private httpClient: HttpClient) { }

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  getAllProductIDs(): Observable<string[]> {
    return this.httpClient.get<string[]>(environment.backend.host + environment.backend.getProducts);
  }

  getProductByID(id: string): Observable<Product> {
    return this.httpClient.get<Product>(environment.backend.host + environment.backend.getProductByID + id);
  }
}
