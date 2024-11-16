import { MessageStatus } from './MessageStatus';

export interface UpdateMessageStatusNotification {
  chatId: number;

  messageStatus: MessageStatus;
}
