import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading: boolean = false;
  constructor(private apiService: ApiService, private router: Router) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
    });
  }
  login() {
    console.log(this.loginForm.value);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.apiService
      .login(this.loginForm.value.email, this.loginForm.value.password)
      .subscribe((res) => {
        // console.log(res);
        if (res.status != 'OK') {
          Swal.fire('error', res.message, 'error');
        } else {
          // console.log(res);
          localStorage.setItem('user', JSON.stringify(res.data.user));
          localStorage.setItem('token', res.data.token);
          this.router.navigate(['/home']);
        }
      });
  }
}
