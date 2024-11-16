import { Message } from './Message';

export interface Chat {
  id: number;
  name: string;
  imageUrl: string;
  numberOfUreadMessages: number;
  lastMessage: Message;
  lastUpdated: Date;
}
