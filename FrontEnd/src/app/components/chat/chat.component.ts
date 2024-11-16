import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Chat } from '../../models/Chat';
import { User } from '../../models/User';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent {
  @Input()
  chat: Chat = {} as Chat;
  user: User | null = {} as User;

  constructor(private dataService: DataService) {
    // this.chat = {
    //   id: 0,
    //   name: 'Mohammed Reda',
    //   imageUrl: 'assets/images/download (1).jpeg',
    //   numberOfUreadMessages: 10,
    //   lastMessage: {
    //     id: 0,
    //     chatId: 0,
    //     content: 'Hello My Friend',
    //     status: MessageStatus.SENT,
    //     sendDateTime: new Date(),
    //     senderId: 0,
    //     receiveDateTime: new Date(),
    //     receiverId: 0,
    //   },
    //   lastUpdated: new Date(),
    // };
    // this.user = {
    //   id: 0,
    //   name: 'Ahmed Reda',
    //   about: "I'm a web developer",
    //   online: true,
    //   imageUrl: '/FrontEnd/public/assets/images/download (2).jpeg',
    //   email: 'o8Lg5@example.com',
    // };
  }

  async ngOnInit() {
    this.user = await this.dataService.getUser();
  }
}
