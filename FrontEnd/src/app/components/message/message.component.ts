import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Message } from '../../models/Message';
import { MessageStatus } from '../../models/MessageStatus';
import { User } from '../../models/User';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './message.component.html',
  styleUrl: './message.component.css',
})
export class MessageComponent {
  @Input()
  message: Message = {} as Message;
  user: User | null = {} as User;
  constructor(private dataService: DataService) {
    // this.message = {
    //   id: 0,
    //   chatId: 0,
    //   content: 'Hello My Friend',
    //   status: MessageStatus.SENT,
    //   sendDateTime: new Date(),
    //   senderId: 0,
    //   receiveDateTime: new Date(),
    //   receiverId: 0,
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
