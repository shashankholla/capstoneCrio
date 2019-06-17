import { Component, OnInit } from '@angular/core';
import { FormsModule, FormControl, FormGroup , Validators} from '@angular/forms';
import { MatCardModule} from '@angular/material';
import { Router } from '@angular/router';
import { ShowOnDirtyErrorStateMatcher} from '@angular/material';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})


export class LoginComponent implements OnInit{
  loginForm : FormGroup;
  esMatcher = new ShowOnDirtyErrorStateMatcher(); 


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
