import { MessageStatus } from './MessageStatus';

export interface Message {
  id: number;
  chatId: number;
  content: string;
  status: MessageStatus;
  sendDateTime: Date;
  senderId: number;
  receiveDateTime: Date;
  receiverId: number;
}
