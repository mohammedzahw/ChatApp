import { Injectable } from '@angular/core';

import { Observable, Subject, firstValueFrom } from 'rxjs';
import { Chat } from '../models/Chat';
import { ChatMessages } from '../models/ChatMessages';
import { Message } from '../models/Message';
import { MessageStatus } from '../models/MessageStatus';
import { MyResponse } from '../models/MyResponse';
import { UpdateMessageStatusNotification } from '../models/UpdateMessageStatusNotification';
import { User } from '../models/User';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  private user: User = {} as User;
  chatsSubject: Subject<Chat[]> = new Subject<Chat[]>();
  messageSubject: Subject<Message> = new Subject<Message>();
  chatMessagesSubject: Subject<UpdateMessageStatusNotification> =
    new Subject<UpdateMessageStatusNotification>();
  chatsMessages: ChatMessages[] = [];
  private chatsDto: Chat[] = [];
  /**************************************************** */
  constructor(private apiService: ApiService) {
    this.OnInit();
  }
  async OnInit() {
    this.user = await this.getUser();
  }
  /***************************************************** */
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
  /******************************************************* */
  setUser(user: User) {
    this.user = user;
    localStorage.setItem('user', JSON.stringify(user));
  }

  /***************************************************************** */
  pushToChatsMessages(message: Message) {
    let index = this.chatsMessages.findIndex(
      (chat) => chat.chatId === message.chatId
    );

    if (index > -1) this.chatsMessages[index].messages.push(message);

    console.log(this.chatsMessages[index]);
  }
  /*********************************************************************** */
  getChats(): Observable<Chat[]> {
    return this.apiService.getUserChats();
  }
  setChats(chats: Chat[]) {
    this.chatsDto = chats;
  }
  /***************************************************************************************** */
  setChatMessages(Notification: UpdateMessageStatusNotification) {
    this.chatMessagesSubject.next(Notification);
  }
  /***************************************************************************************** */
  getChatMessages(chatId: number): Observable<MyResponse<Message[]>> {
    return this.apiService.getChatMessages(chatId);
  }
  /***************************************************************************************** */
  receiveUserMessages() {
    this.apiService.receiveUserMessages().subscribe((res) => {});
  }
  /***************************************************************************************** */
  async getChatMessagesByChatId(chatId: number): Promise<Message[]> {
    if (!chatId) return [];
    let messages: Message[] = [];
    let index = this.chatsMessages.findIndex((chat) => chat.chatId === chatId);
    console.log(index);
    if (index > -1) {
      messages = this.chatsMessages[index].messages;
    } else {
      console.log(chatId);
      let res = await firstValueFrom(this.getChatMessages(chatId));
      console.log(res);
      this.chatsMessages.push({
        chatId: chatId,
        messages: res.data,
      });
      messages = res.data;
    }
    return messages;
  }
  /***************************************************************************************** */
  updateChats(lastMessage: Message) {
    for (let index = 0; index < this.chatsDto.length; index++) {
      if (this.chatsDto[index].id === lastMessage.chatId) {
        let newChat = this.chatsDto[index];
        newChat.lastMessage = lastMessage;
        newChat.lastUpdated = new Date(lastMessage.sendDateTime);
        if (this.user.id !== lastMessage.senderId) {
          newChat.numberOfUreadMessages++;
        }
        this.chatsDto.splice(index, 1);
        this.chatsDto.unshift(newChat);
        this.chatsSubject.next(this.chatsDto);
        break;
      }
    }
  }
  /***************************************************************************************** */
  pushMessage(message: Message) {
    console.log(message.receiverId);
    console.log(this.user.id);
    if (message.receiverId === this.user.id) {
      message.status = MessageStatus.RECEIVED;
      message.receiveDateTime = new Date();
      this.updateChats(message);
      this.messageSubject.next(message);

      this.apiService
        .updateTextMessage(message.id, message.status, message.receiveDateTime)
        .subscribe((res) => {
          console.log(res);
        });
    }
    // this.messageSubject.next(message);
  }
  /***************************************************************************************** */
  createChat(chat: Chat) {
    if (this.chatsDto.some((x) => x.id === chat.id)) {
      return;
    }
    this.chatsDto.unshift(chat);
    this.chatsSubject.next(this.chatsDto);
  }
  /***************************************************************************************** */
}


