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
  }

  async ngOnInit() {
    this.user = await this.dataService.getUser();
  }
}
