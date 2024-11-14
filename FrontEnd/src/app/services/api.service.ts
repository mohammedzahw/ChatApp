import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MyResponse } from '../models/my-response';
import { User } from '../models/user';
import { LoginResponse } from '../models/LoginResponse';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private http: HttpClient) {}

  /*************************************************************************************************** */

  signUp(form: FormData): Observable<MyResponse<null>> {
    return this.http.post<MyResponse<null>>(
      'http://localhost:8080/api/signup',
      form
    );
  }

  /************************************************************************************************* */
  getCurrentUser(): Observable<MyResponse<User>> {
    return this.http.get<MyResponse<User>>(
      'http://localhost:8080/user/current-user'
    );
  }
  /************************************************************************************************* */
  login(
    email: string,
    password: string
  ): Observable<MyResponse<LoginResponse>> {
    return this.http.post<MyResponse<LoginResponse>>(
      'http://localhost:8080/api/login/custom',
      {
        email,
        password,
      }
    );
  }
  /************************************************************************************************* */
  forgetPassword(
    email: string,
    password: string,
    confirmPassword: string
  ): Observable<MyResponse<null>> {
    return this.http.post<MyResponse<null>>(
      'http://localhost:8080/api/forget-password',
      {
        email,
        password,
        confirmPassword,
      }
    );
  }
  /************************************************************************************************* */
}
