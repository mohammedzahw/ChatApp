import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import Swal from 'sweetalert2';
import { ApiService } from '../../services/api.service';
@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    RouterOutlet,
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css',
})
export class RegistrationComponent {
  form: FormGroup;
  constructor(private api: ApiService, private router: Router) {
    this.form = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(3)]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [
        Validators.required,
        Validators.pattern(/(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[^a-zA-Z]).{8,}/),
      ]),
      about: new FormControl(''),
      checkbox: new FormControl(false),
    });
  }
  isCkecked: boolean = false;
  file!: File;
  imageUrl: string | ArrayBuffer | null = null;
  isLoading: boolean = false;
  onFileSelected($event: any) {
    if ($event.target.files.length > 0) {
      console.log($event.target);
      this.file = $event.target.files[0];
      console.log(this.file);
    }

    if (this.file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imageUrl = reader.result;
      };
      reader.readAsDataURL(this.file);
    }
    console.log(this.imageUrl);
  }
  onCheckboxClick() {
    this.form.get('checkbox')?.markAsTouched();
    if (this.form.get('checkbox')?.value)
      this.form.get('checkbox')?.setValue(false);
    else this.form.get('checkbox')?.setValue(true);
  }

  signUp() {
    if (this.form.invalid || !this.form.get('checkbox')?.value) {
      this.form.markAllAsTouched();
      return;
    }
    this.isLoading = true;

    const formData = new FormData();
    formData.append('name', this.form.value.name);
    formData.append('email', this.form.value.email);
    formData.append('password', this.form.value.password);
    formData.append('about', this.form.value.about);
    if (this.file) formData.append('image', this.file);

    this.api.signUp(formData).subscribe((res) => {
      if (res.status === 'OK') {
        Swal.fire(
          'Success',
          'Email Sent Successfully! Please Check Your Email',
          'success'
        );
        this.router.navigate(['/login']);
        console.log(res);
      } else {
        console.log(res);
        Swal.fire('Error', res.message, 'error');
      }
      this.isLoading = false;
    });
  }
}
