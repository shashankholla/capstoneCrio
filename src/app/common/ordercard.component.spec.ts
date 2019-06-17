import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdercardComponent } from './ordercard.component';

describe('OrdercardComponent', () => {
  let component: OrdercardComponent;
  let fixture: ComponentFixture<OrdercardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrdercardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrdercardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
