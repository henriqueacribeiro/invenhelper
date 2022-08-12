import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductStatisticsComponent } from './product-statistics.component';

describe('ProductStatisticsComponent', () => {
  let component: ProductStatisticsComponent;
  let fixture: ComponentFixture<ProductStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductStatisticsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
