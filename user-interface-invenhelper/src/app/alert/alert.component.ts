import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import $ from "jquery";

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss']
})
export class AlertComponent implements OnInit {

  @Input() alertType: string = "";

  @Input() alertMessage: string = "";

  @Output() alertTimeout = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
    setTimeout(() => this.sendAlertTimeout(), 2000);
  }

  sendAlertTimeout(): void{
    $(".alert-fixed").slideUp(1000);
    this.alertTimeout.emit(true);
  }

}
