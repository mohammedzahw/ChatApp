import { Message } from './Message';

export interface ChatMessages {
  chatId: number;
  messages: Message[];
}
