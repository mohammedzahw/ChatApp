import { MessageStatus } from './MessageStatus';

export interface UpdateMessageStatusNotification {
  chatId: number;
  messageId: number;
  messageStatus: MessageStatus;
}
