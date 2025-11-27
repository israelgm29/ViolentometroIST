import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenderSelect } from './gender-select';

describe('GenderSelect', () => {
  let component: GenderSelect;
  let fixture: ComponentFixture<GenderSelect>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenderSelect]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenderSelect);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
