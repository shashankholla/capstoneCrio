import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, FormControl, FormGroup , Validators} from '@angular/forms';
import { MatCardModule} from '@angular/material';
import { Router } from '@angular/router';
import { ShowOnDirtyErrorStateMatcher} from '@angular/material';
import { MenucardComponent } from '../common/menucard.component';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})


export class LoginComponent implements OnInit{
  loginForm : FormGroup;
  esMatcher = new ShowOnDirtyErrorStateMatcher(); 
  
  itemName : string = "idli";
  itemPrice : string = "10";
  isAvailable : boolean = true;
  url : string = "https://cdn-images-1.medium.com/max/1000/1*v79_JDAJ3VCu6L2sp0Qy9A.jpeg";
  ngOnInit(){
    this.loginForm = new FormGroup({
      username : new FormControl(),
      password : new FormControl()
    });
  }

  submit(){
    console.log(this.loginForm.controls.password)
  }
}
