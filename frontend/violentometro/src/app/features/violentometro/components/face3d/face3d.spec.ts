import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Face3d } from './face3d';

describe('Face3d', () => {
  let component: Face3d;
  let fixture: ComponentFixture<Face3d>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Face3d]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Face3d);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
