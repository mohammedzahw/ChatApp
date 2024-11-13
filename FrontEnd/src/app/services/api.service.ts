import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MyResponse } from '../models/my-response';

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
}
