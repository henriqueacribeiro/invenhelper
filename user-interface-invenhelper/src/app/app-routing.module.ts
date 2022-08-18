import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {HomeComponent} from "./home/home.component";
import {ProductHomeComponent} from "./product-home/product-home.component";

const routes: Routes = [

  { path: '', component: HomeComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'product', component: ProductHomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
