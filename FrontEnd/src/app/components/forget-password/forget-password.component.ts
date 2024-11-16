import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import Swal from 'sweetalert2';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-forget-password',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forget-password.component.html',
  styleUrl: './forget-password.component.css',
})
export class ForgetPasswordComponent {
  form: FormGroup;
  isLoading: boolean = false;
  constructor(private apiService: ApiService, private router: Router) {
    this.form = new FormGroup(
      {
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [
          Validators.required,
          Validators.pattern(/(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[^a-zA-Z]).{8,}/),
        ]),
        confirmPassword: new FormControl('', [Validators.required]),
      },
      { validators: this.passwordMatchValidator }
    );
  }
  submit() {
    if (this.form.invalid || this.form.errors?.['passwordMismatch']) {
      this.form.markAllAsTouched();
      return;
    }
    console.log(this.form.value);

    this.isLoading = true;

    this.apiService
      .forgetPassword(
        this.form.value.email,
        this.form.value.password,
        this.form.value.confirmPassword
      )
      .subscribe((res) => {
        console.log(res);
        if (res.status != 'OK') {
          Swal.fire('error', res.message, 'error');
        } else {
          Swal.fire('success', res.message, 'success');
          this.router.navigate(['/login']);
        }
      });
    this.isLoading = false;
  }
  passwordMatchValidator = Validators.compose([
    (control: AbstractControl): { passwordMismatch: boolean } | null => {
      const formGroup = control as FormGroup;
      const password = formGroup.get('password')?.value;
      const confirmPassword = formGroup.get('confirmPassword')?.value;
      if (password !== confirmPassword) {
        return { passwordMismatch: true };
      }
      return null;
    },
  ]);
}
