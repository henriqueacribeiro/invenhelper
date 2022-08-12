import { Component } from '@angular/core';
import {faBoxesStacked, faGaugeHigh, faHouse, faTruckMoving} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'InvenHelper';

  faDashboard = faGaugeHigh;
  faHome = faHouse;
  faProducts = faBoxesStacked;
  faLogo = faTruckMoving;
}
