import { Component, inject } from '@angular/core';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';

@Component({
  selector: 'app-mfa-not-enabled-page-component',
  imports: [MatIconModule],
  templateUrl: './mfa-not-enabled-page-component.html',
})
export class MfaNotEnabledPageComponent {
  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer
  ) {
    this.matIconRegistry.addSvgIcon(
      'email-mfa',
      this.domSanitizer.bypassSecurityTrustResourceUrl('mfa/email.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'sms-mfa',
      this.domSanitizer.bypassSecurityTrustResourceUrl('mfa/sms.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'authenticator-mfa',
      this.domSanitizer.bypassSecurityTrustResourceUrl('mfa/authenticator.svg')
    );
  }

  private readonly router = inject(Router);

  MfaChallengeType = EMfaChallengeType;
  onTriggerMfa(method: EMfaChallengeType) {
    console.log(method);
    this.router.navigate(['/signin/mfa/verify'], {
      state: { type: method },
    });
  }
}

enum EMfaChallengeType {
  EMAIL = 'email',
  SMS = 'sms',
  AUTHENTICATOR = 'totp',
  BIOEMTRIC = 'biometric',
}
