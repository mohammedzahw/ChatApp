import { Injectable, OnInit } from '@angular/core';
import { User } from '../models/User';
import { BehaviorSubject } from 'rxjs';
import { ApiService } from './api.service';
import { DataService } from './data.service';
import { Message } from '../models/Message';
import { UpdateMessageStatusNotification } from '../models/UpdateMessageStatusNotification';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService implements OnInit {
  private user: User | null = {} as User;
  private stompClient: any;
  private connected$ = new BehaviorSubject<boolean>(false);

  constructor(
    private dataService: DataService,
    private apiService: ApiService
  ) {}
  async ngOnInit() {
    this.user = await this.dataService.getUser();
  }

  public connect(): void {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect(
      {},
      (frame: any) => {
        console.log('Connected to WebSocket: ', frame);
        this.connected$.next(true);
      },
      (error: any) => {
        console.error('Connection error: ', error);
      }
    );
  }
  public subscribeToChat(url: string) {
    this.connected$.subscribe((connected) => {
      if (connected) {
        this.stompClient.subscribe(
          url,
          (message: any) => {
            const messageDto: Message = JSON.parse(message.body);
            console.log('messageReceivedOnchat', messageDto);
            this.dataService.pushMessage(messageDto);
          },
          (error: any) => {
            console.error('Subscription error: ', error);
          }
        );
        this.apiService.receiveUserMessages();
      }
    });
  }
  public subscribeToMessageNotification(url: string) {
    this.connected$.subscribe((connected) => {
      if (connected) {
        this.dataService.receiveUserMessages();
        this.stompClient.subscribe(
          url,
          (message: any) => {
            const messageNotification: UpdateMessageStatusNotification =
              JSON.parse(message.body);
            console.log('MessageNotification', messageNotification);
            this.dataService.setChatMessages(messageNotification);
            // this.dataService.pushMessage(messageDto);
          },
          (error: any) => {
            console.error('Subscription error: ', error);
          }
        );
      }
    });
  }
}
