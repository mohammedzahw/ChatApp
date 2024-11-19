import { Injectable, OnInit } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import SockJS from 'sockjs-client';
import { Message } from '../models/Message';
import { UpdateMessageStatusNotification } from '../models/UpdateMessageStatusNotification';
import { User } from '../models/User';
import { ApiService } from './api.service';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService implements OnInit {
  private user: User | null = {} as User;
  private stompClient: any;
  private connected$ = new BehaviorSubject<boolean>(false);


  /****************************************************************************/

  constructor(
    private dataService: DataService,
    private apiService: ApiService
  ) {}

  /****************************************************************************/
  async ngOnInit() {
    this.user = await this.dataService.getUser();
  }

  /****************************************************************************/
  public connect(): void {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.reconnect_delay = 500;
    this.stompClient.heartbeat.outgoing = 2000;
    this.stompClient.heartbeat.incoming = 2000;
    this.stompClient.connect(
      {},
      (frame: any) => {
        console.log('Connected to WebSocket: ', frame);
        this.connected$.next(true);
      },
      (error: any) => {
        console.error('Connection error: ', error);
        this.connected$.next(false);
      }
    );
  }
  /****************************************************************************/

  public subscribeToChat(url: string) {
    this.connected$.subscribe((connected) => {
      if (connected) {
        this.stompClient.subscribe(
          url,
          (message: any) => {
            const messageDto: Message = JSON.parse(message.body);
            this.dataService.pushMessage(messageDto);
          },
          (error: any) => {
            console.error('Subscription error: ', error);
          }
        );
      }
    });
  }
  /****************************************************************************/

  public subscribeToMessageNotification(url: string) {
    this.connected$.subscribe((connected) => {
      if (connected) {
        this.stompClient.subscribe(
          url,
          (message: any) => {
            const messageNotification: UpdateMessageStatusNotification =
              JSON.parse(message.body);
            console.log("messageNotification", messageNotification);
            this.dataService.setChatMessages(messageNotification);
          },
          (error: any) => {
            console.error('Subscription error: ', error);
          }
        );
      }
    });
  }

  /****************************************************************************/
  public disconnect(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected');
        this.connected$.next(false);
      });
    }
  }

}
/****************************************************************************/
/****************************************************************************/
/****************************************************************************/
