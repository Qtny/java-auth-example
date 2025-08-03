import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyMfaPageComponent } from './verify-mfa-page-component';

describe('VerifyMfaPageComponent', () => {
  let component: VerifyMfaPageComponent;
  let fixture: ComponentFixture<VerifyMfaPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyMfaPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerifyMfaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
