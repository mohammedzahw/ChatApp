import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Chat } from '../models/Chat';
import { LoginResponse } from '../models/LoginResponse';
import { Message } from '../models/Message';
import { MessageStatus } from '../models/MessageStatus';
import { MyResponse } from '../models/MyResponse';
import { SendMessageRequest } from '../models/SendMessageRequest';
import { User } from '../models/User';

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

  getChatMessages(chatId: number): Observable<MyResponse<Message[]>> {
    if (!chatId) {
      return new Observable((observer) => {});
    }
    return this.http.get<MyResponse<Message[]>>(
      `http://localhost:8080/api/messages/${chatId}`
    );
  }

  /****************************************************************************************************/
  sendMessage(message: SendMessageRequest): Observable<MyResponse<Message>> {
    console.log(message);
    return this.http.post<MyResponse<Message>>(
      `http://localhost:8080/api/send-message/text`,
      message
    );
  }

  /****************************************************************************************************/
  setUserOnline(userId: number): Observable<MyResponse<null>> {
    return this.http.get<MyResponse<null>>(
      `http://localhost:8080/user/online/${userId}`
    );
  }
  /****************************************************************************************************/
  setUserOffline(userId: number): Observable<MyResponse<null>> {
    return this.http.get<MyResponse<null>>(
      'http://localhost:8080/user/offline/' + userId
    );
  }
  /****************************************************************************************************/
  readChatMessages(chatId: number): Observable<MyResponse<null>> {
    if (!chatId) {
      return new Observable((observer) => {});
    }
    // console.log(chatId);
    return this.http.get<MyResponse<null>>(
      `http://localhost:8080/api/chat/read-messages/${chatId}`
    );
  }
  /****************************************************************************************************/
  receiveUserMessages(): Observable<MyResponse<null>> {
    return this.http.get<MyResponse<null>>(
      `http://localhost:8080/api/messages/receive`
    );
  }
  /****************************************************************************************************/
  updateTextMessage(
    messageId: number,
    status: MessageStatus,
    time: Date
  ): Observable<MyResponse<null>> {
    return this.http.post<MyResponse<null>>(
      `http://localhost:8080/api/text-message/update-status`,
      { messageId, status, time }
    );
  }
  /****************************************************************************************************/
  searchUsers(searchText: string): Observable<MyResponse<User[]>> {
    return this.http.get<MyResponse<User[]>>(
      `http://localhost:8080/user/search/${searchText}`
    );
  }
  getChat(chatId: number): Observable<MyResponse<Chat>> {
    return this.http.get<MyResponse<Chat>>(
      `http://localhost:8080/chat/${chatId}`
    );
  }
  /****************************************************************************************************/

  getUserChats(): Observable<Chat[]> {
    return this.http.get<Chat[]>('http://localhost:8080/chat/user-chats');
  }
  /****************************************************************************************************/
  searchChat(userId: number): Observable<MyResponse<Chat>> {
    return this.http.get<MyResponse<Chat>>(
      `http://localhost:8080/chat/search-chat/${userId}`
    );
  }
  /****************************************************************************************************/
  deleteChatMessages(chatId: number): Observable<MyResponse<null>> {
    return this.http.delete<MyResponse<null>>(
      `http://localhost:8080/chat/delete-messages/${chatId}`
    );
  }
  /****************************************************************************************************/
  deleteChat(chatId: number): Observable<MyResponse<null>> {
    return this.http.delete<MyResponse<null>>(
      `http://localhost:8080/chat/delete/${chatId}`
    );
  }
  /****************************************************************************************************/
  deleteMessage(messageId: number): Observable<MyResponse<null>> {
    return this.http.delete<MyResponse<null>>(
      `http://localhost:8080/api/text-message/delete/${messageId}`
    );
  }
  /****************************************************************************************************/
}
