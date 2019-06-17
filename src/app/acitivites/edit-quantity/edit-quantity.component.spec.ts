import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditQuantityComponent } from './edit-quantity.component';

describe('EditQuantityComponent', () => {
  let component: EditQuantityComponent;
  let fixture: ComponentFixture<EditQuantityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditQuantityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditQuantityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
