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
  constructor(private dataService: DataService) {}
  /********************************************************* */
  async ngOnInit() {
    this.user = await this.dataService.getUser();
  }
  /********************************************************* */
  deleteMessage() {
    this.dataService.deleteMessage(this.message.id, this.message.chatId);
  }
  /********************************************************* */
}
