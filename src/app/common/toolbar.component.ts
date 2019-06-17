import { Component, OnInit } from '@angular/core';
import {MatButton} from '@angular/material';
import { Route, Router } from '@angular/router';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {
backShow : boolean = false;
  constructor(public router : Router) { }

  ngOnInit() {
    if(this.router.url == "results")
    {
      this.backShow = true;
    }
    else{
      this.backShow = false;
    }
  }

}
