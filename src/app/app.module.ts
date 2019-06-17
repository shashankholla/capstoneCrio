import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomepageComponent } from './activities/homepage/homepage.component';
import { ToolbarComponent } from './common/toolbar.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { MatCardModule, MatIcon, MatIconModule, MatToolbarModule, MatButtonModule, MatInputModule, ErrorStateMatcher } from '@angular/material';
import { RegisterComponent } from './register/register.component';
import { ShowOnDirtyErrorStateMatcher} from '@angular/material';
import {DemoMaterialModule} from './material-module';
import { MenucardComponent } from './common/menucard.component';
import { OrdercardComponent } from './common/ordercard.component';
import { EditMenuComponent } from './acitivites/edit-menu/edit-menu.component';
import { EditQuantityComponent } from './acitivites/edit-quantity/edit-quantity.component';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomepageComponent,
    ToolbarComponent,
    RegisterComponent,
    MenucardComponent,
    OrdercardComponent,
    EditMenuComponent,
    EditQuantityComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatInputModule,
    DemoMaterialModule
           
  ],
  providers: [{provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher}],
  bootstrap: [AppComponent]
})
export class AppModule { }
