import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { LoginState } from '../../../store/login/login.reducer';
import { distinctUntilChanged, filter, Subject, takeUntil } from 'rxjs';
import { EMfaChallengeType } from '../../../../models/mfa.model';
import {
  initiateEmailMfaAction,
  initiateTotpMfaAction,
} from '../../../store/login/login.action';
import { openErrorDialogAction } from '../../../store/global/global.action';
import {
  selectError,
  selectMfaType,
} from '../../../store/login/login.selector';

@Component({
  selector: 'app-mfa-not-enabled-page-component',
  imports: [MatIconModule],
  templateUrl: './mfa-not-enabled-page-component.html',
})
export class MfaNotEnabledPageComponent implements OnInit, OnDestroy {
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
  private readonly store = inject(Store<LoginState>);
  private readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    // navigation
    this.store
      .select(selectMfaType)
      .pipe(
        distinctUntilChanged((type) => type != null),
        filter((type) => type != null),
        takeUntil(this.destroy$)
      )
      .subscribe((type) => {
        if (type != null) {
          this.router.navigate(['/signin/mfa/verify']);
        }
      });
    // error handling
    this.store
      .select(selectError)
      .pipe(takeUntil(this.destroy$))
      .subscribe((error) => {
        if (error) {
          this.store.dispatch(
            openErrorDialogAction({
              title: "We can't proceed without your help",
              description: error,
            })
          );
          console.error('error during creation => ', error);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  MfaChallengeType = EMfaChallengeType;
  onTriggerMfa(method: EMfaChallengeType) {
    switch (method) {
      case EMfaChallengeType.EMAIL:
        this.store.dispatch(initiateEmailMfaAction());
        break;
      case EMfaChallengeType.TOTP:
        this.store.dispatch(initiateTotpMfaAction());
        break;
      default:
        console.error('invalid mfa method');
    }
  }
}
