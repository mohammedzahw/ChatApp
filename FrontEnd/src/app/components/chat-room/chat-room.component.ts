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
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { DataService } from '../../services/data.service';

import { Chat } from '../../models/Chat';
import { Message } from '../../models/Message';
import { MessageStatus } from '../../models/MessageStatus';
import { User } from '../../models/User';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  /*************************************************************************** */
  constructor(
    private apiService: ApiService,
    private dataService: DataService
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
  async ngOnChanges(changes: SimpleChanges) {
    if (changes && changes['chat'].currentValue) {
      this.message = '';
      this.chat = changes['chat'].currentValue;
      if (this.chat.numberOfUreadMessages > 0) {
        this.chat.numberOfUreadMessages = 0;
        await firstValueFrom(this.apiService.readChatMessages(this.chat.id));
      }
      this.dataService
        .getChatMessagesByChatId(this.chat.id)
        .then((messages) => {
          this.chatMessages = messages;
          // this.chatMessages.reverse();
        });

      // console.log(this.chat);
    }
  }
  /*************************************************************************** */
  async ngOnInit() {
    //get the current user
    this.dataService.chatMessagesSubject.subscribe((notify) => {
      if (notify.messageStatus === MessageStatus.READ) {
        this.chat.numberOfUreadMessages = 0;
      }
      if (notify.chatId === this.chat.id) {
        for (let i = 0; i < this.chatMessages.length; i++) {
          if (this.chatMessages[i].status !== MessageStatus.READ) {
            this.chatMessages[i].status = notify.messageStatus;
            this.chatMessages[i].receiveDateTime = new Date();
          }
        }
      }
    });
    this.user = await this.dataService.getUser();

    //subscribe to the new messages
    this.dataService.messageSubject.subscribe((message) => {
      // if the new message is for the current chat
      if (message.chatId === this.chat.id) {
        //change the status to read and the receive date time to now
        message.status = MessageStatus.READ;
        message.receiveDateTime = new Date();
        this.chat.numberOfUreadMessages = -1;

        this.dataService.updateChats(message);
        //push the message to the chat messages
        this.dataService.pushToChatsMessages(message);
        //set the chat messages in the data service
        this.apiService.updateTextMessage(
          message.id,
          message.status,
          message.receiveDateTime
        );
      }

      // }
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
        console.log(res);
        this.dataService.pushToChatsMessages(res.data);
        // this.chat.numberOfUreadMessages--;
        this.dataService.updateChats(res.data);
        this.message = '';
      });
  }

  /**************************************************************************** */
}
