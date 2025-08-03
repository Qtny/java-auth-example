import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MfaNotEnabledPageComponent } from './mfa-not-enabled-page-component';

describe('MfaNotEnabledPageComponent', () => {
  let component: MfaNotEnabledPageComponent;
  let fixture: ComponentFixture<MfaNotEnabledPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MfaNotEnabledPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MfaNotEnabledPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
