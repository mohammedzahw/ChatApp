import { Injectable } from '@angular/core';

import { firstValueFrom, Observable, Subject } from 'rxjs';
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
  chatMessagesSubject: Subject<ChatMessages> = new Subject<ChatMessages>();
  chatsMessages: ChatMessages[] = [];
  public chatsDto: Chat[] = [];
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
      const res = await firstValueFrom(this.apiService.getCurrentUser());
      this.user = res.data;
      localStorage.setItem('user', JSON.stringify(this.user));
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

  // //push message to the chat
  pushToChatsMessages(message: Message) {
    this.chatsMessages.forEach((chat) => {
      if (chat.chatId === message.chatId) {
        chat.messages.push(message);
        this.chatMessagesSubject.next(chat);
      }
    });
  }
  /*********************************************************************** */
  getChats(): Observable<Chat[]> {
    return this.apiService.getUserChats();
  }
  setChats(chats: Chat[]) {
    this.chatsDto = chats;
  }
  /***************************************************************************************** */

  updateMessageStatus(notify: UpdateMessageStatusNotification) {
    let chatIndex = this.chatsDto.findIndex(
      (chat) => chat.id === notify.chatId
    );
    // console.log('notify', notify);
    this.chatsMessages.forEach((chat) => {
      if (chat.chatId === notify.chatId) {
        chat.messages.map((message) => {
          if (message.id === notify.messageId) {
            message.status = notify.messageStatus;
          }
        });
        this.chatMessagesSubject.next(chat);
      }
    });
    this.chatsSubject.next(this.chatsDto);
  }
  /*************************************************************************** */
  deletMessage(notify: UpdateMessageStatusNotification) {
    //get the chat index in chatsDto list
    let chatIndex = this.chatsDto.findIndex(
      (chat) => chat.id === notify.chatId
    );
    //loop through chatsMessages if find the chat delete the message from it and check if the message is read
    for (let i = 0; i < this.chatsMessages.length; i++) {
      if (this.chatsMessages[i].chatId === notify.chatId) {
        let isMessageRead = true;
        this.chatsMessages[i].messages = this.chatsMessages[i].messages.filter(
          (message) => {
            if (message.id === notify.messageId) {
              if (message.status !== MessageStatus.READ) isMessageRead = false;
              return false;
            }
            return true;
          }
        );

        if (!isMessageRead) {
          this.chatsDto[chatIndex].numberOfUreadMessages--;
        }

        //update last message

        if (this.chatsDto[chatIndex].lastMessage.id === notify.messageId) {
          if (this.chatsMessages[i].messages.length > 0) {
            this.chatsDto[chatIndex].lastMessage =
              this.chatsMessages[i].messages.slice(-1)[0];
          } else {
            this.chatsDto[chatIndex].lastMessage = {} as Message;
          }
        }
        this.chatsSubject.next(this.chatsDto);
        this.chatMessagesSubject.next(this.chatsMessages[i]);
        break;
      }
    }
  }

  /*************************************************************************** */
  deleteCurrentChat(notify: UpdateMessageStatusNotification) {
    let chatIndex = this.chatsMessages.findIndex(
      (chat) => chat.chatId === notify.chatId
    );

    this.chatsDto = this.chatsDto.filter((chat) => chat.id !== notify.chatId);

    this.chatsMessages = this.chatsMessages.filter(
      (chat) => chat.chatId !== notify.chatId
    );
    this.chatsSubject.next(this.chatsDto);
    this.chatMessagesSubject.next(this.chatsMessages[chatIndex]);
  }
  /***************************************************************************************** */
  clearCurrentChat(notify: UpdateMessageStatusNotification) {
    let chatIndex = this.chatsDto.findIndex(
      (chat) => chat.id === notify.chatId
    );

    this.chatsMessages.map((chat) => {
      if (chat.chatId === notify.chatId) {
        chat.messages = [];
        this.chatMessagesSubject.next(chat);
      }
    });
    this.chatsDto[chatIndex].lastMessage = {} as Message;
    this.chatsSubject.next(this.chatsDto);
  }
  /***************************************************************************************** */
  setChatMessages(notify: UpdateMessageStatusNotification) {
    if (notify.messageId !== -1) {
      if (notify.messageStatus === MessageStatus.DELETED) {
        this.deletMessage(notify);
      } else {
        this.updateMessageStatus(notify);
      }
    } else {
      // console.log('notify', notify);
      if (notify.messageStatus === MessageStatus.DELETED) {
        this.deleteCurrentChat(notify);
      } else if (notify.messageStatus === MessageStatus.CLEAR) {
        this.clearCurrentChat(notify);
      } else {
        this.UpdateChatMessages(notify);
      }
    }
  }
  /***************************************************************************************** */
  getChatMessages(chatId: number): Observable<MyResponse<Message[]>> {
    return this.apiService.getChatMessages(chatId);
  }
  /***************************************************************************************** */
  receiveUserMessages() {
    this.apiService.receiveUserMessages().subscribe((res) => {
    });
  }
  /***************************************************************************************** */
  async getChatMessagesByChatId(chatId: number) {
    if (!chatId) return;

    let index = this.chatsMessages.findIndex((chat) => chat.chatId === chatId);

    if (index == -1) {
      let res = await firstValueFrom(this.getChatMessages(chatId));
      this.chatsMessages.push({
        chatId: chatId,
        messages: res.data,
      });
      this.chatMessagesSubject.next(this.chatsMessages.slice(-1)[0]);
    } else {
      this.chatMessagesSubject.next(this.chatsMessages[index]);
    }
  }
  /***************************************************************************************** */
  updateChats(lastMessage: Message) {
    this.chatsDto.map((chat) => {
      if (chat.id === lastMessage.chatId) {
        chat.lastMessage = lastMessage;
        chat.lastUpdated = new Date(lastMessage.sendDateTime);
        if (this.user.id !== lastMessage.senderId) {
          chat.numberOfUreadMessages++;
        }
      }
    });
  }

  /***************************************************************************************** */
  // receive Message from server

  pushMessage(message: Message) {
    // if the Current user is the receiver
    if (message.receiverId === this.user.id) {
      message.status = MessageStatus.RECEIVED;
      message.receiveDateTime = new Date();
      this.chatsMessages.forEach((chat) => {
        if (chat.chatId === message.chatId) {
          chat.messages.push(message);
          this.chatMessagesSubject.next(chat);
        }
      });
      // console.log(this.chatsMessages);
      this.updateChats(message);

      //send message to the current chat
      this.messageSubject.next(message);

      this.apiService
        .updateTextMessage(message.id, message.status, message.receiveDateTime)
        .subscribe((res) => {
          console.log(res);
        });
    }
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

  /***************************************************************************************** */
  deleteChat(chatId: number) {
    this.apiService.deleteChat(chatId).subscribe((res) => {
      this.deleteCurrentChat({
        chatId: chatId,
        messageId: -1,
        messageStatus: MessageStatus.DELETED,
      });
    });
  }
  /***************************************************************************************** */
  clearChat(chatId: number) {
    this.apiService.deleteChatMessages(chatId).subscribe((res) => {
      this.clearCurrentChat({
        chatId: chatId,
        messageId: -1,
        messageStatus: MessageStatus.DELETED,
      });
    });
  }
  /***************************************************************************************** */
  deleteMessage(messageId: number, chatId: number) {
    this.apiService.deleteMessage(messageId).subscribe((res) => {
      this.deletMessage({
        chatId: chatId,
        messageId: messageId,
        messageStatus: MessageStatus.DELETED,
      });
    });
  }
  /***************************************************************************************** */

  // read chat message in the client
  UpdateChatMessages(notify: UpdateMessageStatusNotification) {
    if (!notify) return;
    // console.log('notify', notify);

    this.chatsMessages.forEach((chat) => {
      if (chat.chatId === notify.chatId) {
        chat.messages.forEach((message) => {
          message.status = notify.messageStatus;
          message.receiveDateTime = new Date();
        });
        this.chatMessagesSubject.next(chat);
      }
    });
  }
  /***************************************************************************************** */
}


