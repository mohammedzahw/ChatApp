import { Component } from '@angular/core';
import { Chat } from '../../models/Chat';
import { User } from '../../models/User';
import { ApiService } from '../../services/api.service';
import { DataService } from '../../services/data.service';
import { WebSocketService } from '../../services/web-socket.service';
import { ChatRoomComponent } from '../chat-room/chat-room.component';
import { ChatComponent } from '../chat/chat.component';
import { MessageStatus } from '../../models/MessageStatus';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ChatComponent, ChatRoomComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  chats: Chat[] = [];
  user: User = {} as User;
  opendChat: Chat = {} as Chat;
  searchResult: User[] = [];
  hideChats: boolean = false;
  hideChat: boolean = true;
  /*************************************************************** */

  constructor(
    private apiService: ApiService,
    private dataService: DataService,
    private wsService: WebSocketService
  ) {
    dataService.chatsSubject.subscribe((req) => {
      this.chats = req;
    });
  }
  /*************************************************************** */
  //search users
  search(event: any) {
    console.log(event.target.value);
    if (event.target.value === '') this.searchResult = [];
    else {
      this.apiService.searchUsers(event.target.value).subscribe((res) => {
        this.searchResult = res.data;
      });
    }
  }
  /*************************************************************** */
  //open chat
  openUserChat(searchUser: User) {
    this.hideChat = false;
    console.log('searchUser', searchUser);
    this.apiService.searchChat(searchUser.id).subscribe((res) => {
      this.opendChat = res.data;
      this.dataService.createChat(res.data);
    });
  }
  /*************************************************************** */
  press(chat: Chat) {
    const isLargeScreen = window.matchMedia('(min-width: 1000px)').matches;

    if (!isLargeScreen) {
      this.hideChats = true;
      this.hideChat = false;
    } else {
      this.hideChats = false;
      this.hideChat = false;
    }
    this.opendChat = chat;

    if (this.opendChat.numberOfUreadMessages > 0) {
      this.apiService.readChatMessages(this.opendChat.id).subscribe((res) => {
        this.dataService.UpdateChatMessages({
          chatId: this.opendChat.id,
          messageId: -1,
          messageStatus: MessageStatus.READ,
        });
      });
      this.opendChat.numberOfUreadMessages = 0;
    }
  }
  /*************************************************************** */
  async ngOnInit() {
    this.apiService.getUserChats().subscribe((chats) => {
      this.chats = chats;
      this.dataService.setChats(chats);
    });
    this.user = await this.dataService.getUser();
    /*************************************************************** */
    // console.log(this.user);
    this.apiService.setUserOnline(this.user.id).subscribe((res) => {});
    this.wsService.connect();
    this.wsService.subscribeToChat(`/topic/user/${this.user.id}`);
    this.wsService.subscribeToMessageNotification(
      `/topic/messages/${this.user.id}`
    );

    this.dataService.receiveUserMessages();

    window.addEventListener('beforeunload', this.setUserOffline.bind(this));
  }
  /*************************************************************** */
  setUserOffline(event: Event): void {
    this.apiService.setUserOffline(this.user.id).subscribe((res) => {});
    this.wsService.disconnect();
  }
  /*************************************************************** */
  ngOnDestroy(): void {
    console.log('AppComponent destroyed');
    if (this.user && this.user.id) {
      window.removeEventListener(
        'beforeunload',
        this.setUserOffline.bind(this)
      );
      this.apiService.setUserOffline(this.user.id).subscribe(
        (res) => {
          console.log('offline', res);
        },
        (error) => {
          console.error('Error setting offline status:', error);
        }
      );
    } else {
      console.warn('User ID is not defined');
    }
  }
  /*************************************************************** */
  displayChats() {
    this.hideChats = !this.hideChats;
    this.hideChat = !this.hideChats;
  }
}
