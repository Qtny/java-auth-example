import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterOtpPageComponent } from './register-otp-page-component';

describe('RegisterOtpPageComponent', () => {
  let component: RegisterOtpPageComponent;
  let fixture: ComponentFixture<RegisterOtpPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterOtpPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterOtpPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
