import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { ApiService } from '../../services/api.service';
import { DataService } from '../../services/data.service';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Chat } from '../../models/Chat';
import { Message } from '../../models/Message';
import { MessageStatus } from '../../models/MessageStatus';
import { User } from '../../models/User';
import { MessageComponent } from '../message/message.component';

@Component({
  selector: 'app-chat-room',
  standalone: true,
  imports: [CommonModule, FormsModule, MessageComponent],
  templateUrl: './chat-room.component.html',
  styleUrl: './chat-room.component.css',
})
export class ChatRoomComponent implements OnInit, OnChanges, AfterViewInit {
  chatMessages: Message[] = [];
  @Input()
  chat: Chat = {} as Chat;
  user: User | null = {} as User;
  message: string = '';
  display: boolean = false;
  /*************************************************************************** */
  constructor(
    private apiService: ApiService,
    private dataService: DataService,
    private router: Router
  ) {}

  /*************************************************************************** */
  @ViewChild('messagesContainer') private messagesContainer: ElementRef =
    {} as ElementRef;
  /*************************************************************************** */
  ngAfterViewInit() {
    const observer = new MutationObserver(() => {
      this.scrollToBottom();
    });
    /*************************************************************************** */
    observer.observe(this.messagesContainer.nativeElement, {
      childList: true,
    });
  }
  /*************************************************************************** */
  private scrollToBottom(): void {
    this.messagesContainer.nativeElement.scrollTop =
      this.messagesContainer.nativeElement.scrollHeight;
  }
  /*************************************************************************** */
  ngOnChanges(changes: SimpleChanges) {
    this.display = false;
    // if the chat is changed
    if (changes && changes['chat'].currentValue) {
      this.message = '';
      this.chat = changes['chat'].currentValue;
      this.ngOnInit();
    }
  }

  /*************************************************************************** */
  async ngOnInit() {
    this.dataService.chatMessagesSubject.subscribe((chatMessages) => {
      if (chatMessages.chatId === this.chat.id)
        this.chatMessages = chatMessages.messages;
      // console.log(this.chatMessages);
    });

    await this.dataService.getChatMessagesByChatId(this.chat.id);
    this.user = await this.dataService.getUser();
    //subscribe to the new messages
    this.dataService.messageSubject.subscribe((message) => {
      // if the new message is for the current chat
      if (message.chatId === this.chat.id) {
        //change the status to read and the receive date time to now
        message.status = MessageStatus.READ;
        message.receiveDateTime = new Date();
        this.chat.numberOfUreadMessages = 0;
        //update the status of the message
        this.dataService.updateMessageStatus({
          chatId: message.chatId,
          messageId: message.id,
          messageStatus: MessageStatus.READ,
        });
        //update the message in the backend
        this.apiService
          .updateTextMessage(
            message.id,
            message.status,
            message.receiveDateTime
          )
          .subscribe((res) => {});
      }
    });
  }
  /*************************************************************************** */
  send(chatId: number) {
    if (!this.message) return;
    this.apiService
      .sendMessage({
        chatId: chatId,
        text: this.message,
      })
      .subscribe((res) => {
        // console.log('res', res);
        this.dataService.pushToChatsMessages(res.data);
        // this.chat.numberOfUreadMessages--;
        this.dataService.updateChats(res.data);
        this.message = '';
      });
  }

  /**************************************************************************** */
  displayOptions() {
    this.display = !this.display;
  }
  /**************************************************************************** */
  clearChat(chatId: number) {
    this.dataService.clearChat(chatId);
    this.display = false;
  }
  /**************************************************************************** */
  async deleteChat(chatId: number) {
    await this.dataService.deleteChat(chatId);
    this.chat = {} as Chat;
    this.display = false;
  }
  /**************************************************************************** */
}
