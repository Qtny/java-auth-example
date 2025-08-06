import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  QueryList,
  signal,
  ViewChildren,
} from '@angular/core';

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

    this.otpInputs.forEach((input) => {
      input.nativeElement.addEventListener(
        'paste',
        (event: ClipboardEvent) => {
          this.onPaste(event);
        },
        true
      );
    });
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

  onPaste(event: ClipboardEvent) {
    event.preventDefault();

    const pasteData = event.clipboardData?.getData('text') || '';
    console.log('[ONPASTE] :: pasted data', pasteData);
    const digits = pasteData.replace(/\D/g, '').slice(0, 6).split('');

    digits.forEach((digit, i) => {
      this.otp[i] = digit;
      const input = this.otpInputs.toArray()[i];
      if (input) {
        input.nativeElement.value = digit;
      }
    });

    const lastIndex = digits.length - 1;
    if (lastIndex < this.otpInputs.length - 1) {
      this.otpInputs.toArray()[lastIndex + 1].nativeElement.focus();
    } else {
      this.otpInputs.toArray()[lastIndex].nativeElement.blur();
    }

    if (this.otp.every((num) => num.length === 1)) {
      this.onSubmitOtp();
    }
  }

  onSubmitOtp() {
    const code = this.otp.join('');
    this.onCompleted.emit(code);
    this.otpInputs.forEach((el) => el.nativeElement.blur());
  }
}
