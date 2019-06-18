import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-menucard',
  templateUrl: './menucard.component.html',
  styleUrls: ['./menucard.component.css']
})
export class MenucardComponent{
  @Input() itemName : string;
  @Input() itemPrice : string;
  @Input() image : string;
  @Input() available : boolean;
  

 
  

}
