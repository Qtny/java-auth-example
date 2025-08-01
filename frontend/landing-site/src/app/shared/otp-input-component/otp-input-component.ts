import { Component, ElementRef, EventEmitter, Input, Output, QueryList, signal, ViewChildren } from '@angular/core';

@Component({
  selector: 'app-otp-input-component',
  imports: [],
  templateUrl: './otp-input-component.html',
})
export class OtpInputComponent {
  @ViewChildren('otp0, otp1, otp2, otp3, otp4, otp5') otpInputs!: QueryList<
    ElementRef<HTMLInputElement>
  >;

  @Input() completed: boolean = false;
  @Output() onCompleted = new EventEmitter<string>();
  protected readonly otp: string[] = new Array(6).fill('');

  ngAfterViewInit() {
    this.otpInputs.first.nativeElement.focus();
  }

  onInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const val = input.value.trim();

    if (/^\d$/.test(val)) {
      this.otp[index] = val;

      // Move to next input
      const nextInput = this.otpInputs.toArray()[index + 1];
      if (nextInput) {
        nextInput.nativeElement.focus();
      } else {
        input.blur(); // Optional: blur on last input
      }
    } else {
      input.value = '';
    }

    if (this.otp.every((num) => num.length === 1)) {
      this.onSubmitOtp();
    }
  }

  onKeyDown(event: KeyboardEvent, index: number): void {
    const input = event.target as HTMLInputElement;

    if (event.key === 'Backspace') {
      this.otp[index] = '';
      if (input.value === '' && index > 0) {
        this.otpInputs.toArray()[index - 1].nativeElement.focus();
      }
    }

    if (event.key.length === 1 && !/^\d$/.test(event.key)) {
      event.preventDefault();
    }
  }

  onSubmitOtp() {
    const code = this.otp.join('');
    this.onCompleted.emit(code);
    this.otpInputs.forEach(el => el.nativeElement.blur());
  }
}
