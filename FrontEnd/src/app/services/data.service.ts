import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  private user: User = {} as User;

  constructor(private apiService: ApiService) {}
  async ngOnInit() {
    this.user = await this.getUser();
  }
  async getUser(): Promise<User> {
    const userString = localStorage.getItem('user');
    let user = {} as User;
    if (!userString) {
      await this.apiService.getCurrentUser().subscribe((res) => {
        this.user = res.data;
        localStorage.setItem('user', JSON.stringify(this.user));
      });
    } else {
      user = JSON.parse(userString);
    }
    return user;
  }
  setUser(user: User) {
    this.user = user;
    localStorage.setItem('user', JSON.stringify(user));
  }
}
