import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { FormsModule, FormControl, FormGroup , Validators} from '@angular/forms';
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  constructor() { }

  registerForm = new FormGroup({
    resName : new FormControl(),
    fullName : new FormControl(),
    address : new FormControl(),
    username : new FormControl()

  }
  )


  ngOnInit() {
  }

}
