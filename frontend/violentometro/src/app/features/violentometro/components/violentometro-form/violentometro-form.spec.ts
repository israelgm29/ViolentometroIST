import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViolentometroForm } from './violentometro-form';

describe('ViolentometroForm', () => {
  let component: ViolentometroForm;
  let fixture: ComponentFixture<ViolentometroForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViolentometroForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViolentometroForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
