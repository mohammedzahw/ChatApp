package com.example.chat.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.chat.dtos.MessageDto;
import com.example.chat.dtos.SendTextMessageRequest;
import com.example.chat.dtos.UpdateMessageStatusDto;
import com.example.chat.dtos.UpdateTextMessageStatusRequest;
import com.example.chat.exception.CustomException;
import com.example.chat.mappers.TextMessageMapper;
import com.example.chat.models.Chat;
import com.example.chat.models.LocalUser;
import com.example.chat.models.MessageStatus;
import com.example.chat.models.TextMessage;
import com.example.chat.repositories.ChatRepository;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.repositories.TextMessageRepository;
import com.example.chat.security.TokenUtil;

import jakarta.transaction.Transactional;

@Service
public class TextMessageService {

        private TextMessageMapper textMessageMapper;

        private TextMessageRepository textMessageRepository;
        private LocalUserRepository localUserRepository;
        private TokenUtil tokenUtil;

        private ChatRepository chatRepository;
        private SimpMessagingTemplate messagingTemplate;

        public TextMessageService(TextMessageMapper textMessageMapper,
                        TextMessageRepository textMessageRepository,
                        LocalUserRepository localUserRepository,
                        TokenUtil tokenUtil,
                        ChatRepository chatRepository, SimpMessagingTemplate messagingTemplate) {
                this.messagingTemplate = messagingTemplate;

                this.tokenUtil = tokenUtil;

                this.chatRepository = chatRepository;

                this.textMessageMapper = textMessageMapper;
                this.textMessageRepository = textMessageRepository;
                this.localUserRepository = localUserRepository;

        }

        /******************************************************************************* */

        public TextMessage getMessageById(Integer int1) {
                try {
                        return textMessageRepository.findById(int1).orElse(null);
                } catch (Exception e) {
                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }
        }

        /******************************************************************************* */

        public List<MessageDto> getMessagesByChatId(Integer chatId) {
                try {
                        return textMessageMapper.toDtoList(textMessageRepository.getMessagesByChatId(chatId), chatId);
                } catch (Exception e) {
                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }
        }

        /**********************************************************************************/

        public Integer getChatId(Integer messageId) {

                return textMessageRepository.getChatId(messageId);
        }

        /***************************************************************************************/
        public void deleteTextMessage(Integer messageId) {
                try {
                        // System.out.println("messageId : " + messageId);
                        TextMessage textMessage = getMessageById(messageId);
                        Integer chatId = getChatId(messageId);
                        textMessageRepository.deleteById(messageId);

                        sendToSocket(chatId, messageId, MessageStatus.DELETED,
                                        textMessage.getReceiver().getId());
                } catch (Exception e) {
                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }
        }

        /***************************************************************************************/
        @Transactional
        public MessageDto sendTextMessage(SendTextMessageRequest sendTextMessageRequest)

                        throws IOException, TimeoutException {
                try {
                        Chat chat = chatRepository.findById(sendTextMessageRequest.getChatId()).orElseThrow(
                                        () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));

                        LocalUser sender = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                                        () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                        LocalUser receiver = chat.getUser1().getId() == sender.getId() ? chat.getUser2()
                                        : chat.getUser1();
                        if (chat.getUser1().getId() != sender.getId() && chat.getUser2().getId() != sender.getId()) {
                                throw new CustomException("You can't send this message", HttpStatus.BAD_REQUEST);
                        }

                        TextMessage textMessage = textMessageMapper.toEntity(sendTextMessageRequest, chat, sender,
                                        receiver);
                        textMessageRepository.save(textMessage);
                        chat.setLastUpdated(LocalDateTime.now());
                        chatRepository.save(chat);

                        MessageDto messageDto = textMessageMapper.toDto(textMessage, chat.getId());

                        messagingTemplate.convertAndSend("/topic/user/" + receiver.getId(), messageDto);
                        return messageDto;
                } catch (Exception e) {
                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }

        }

        /***************************************************************************************/
        @Transactional
        public MessageDto updateTextMessageStatus(UpdateTextMessageStatusRequest message) {
                try {

                        TextMessage textMessage = getMessageById(message.getMessageId());

                        if (textMessage == null) {
                                throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
                        }

                        textMessage.setStatus(message.getStatus());
                        textMessage.setReceiveDateTime(message.getTime());
                        Integer chatId = getChatId(message.getMessageId());

                        textMessageRepository.save(textMessage);

                        sendToSocket(chatId, message.getMessageId(), message.getStatus(),
                                        textMessage.getSender().getId());

                        return textMessageMapper.toDto(textMessage, getChatId(message.getMessageId()));
                } catch (Exception e) {
                        throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
                }

        }

        /******************************************************************************* */
        public void setChatMessagesToRead(Integer chatId) {
                try {
                        Integer userId = tokenUtil.getUserId();
                        textMessageRepository.setChatMessagesToRead(chatId,
                                        userId);

                        Chat chat = chatRepository.findById(chatId).orElseThrow(
                                        () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));
                        Integer senderId = chat.getUser1().getId().equals(userId) ? chat.getUser2().getId()
                                        : chat.getUser1().getId();

                        sendToSocket(chatId, -1, MessageStatus.READ, senderId);

                } catch (Exception e) {
                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }
        }

        /******************************************************************************* */
        public void setUserMessagesToReceive() {
                try {
                        Integer userId = tokenUtil.getUserId();
                        textMessageRepository.setUserMessagesToReceive(
                                        userId);
                        List<Chat> userChat = chatRepository
                                        .findChatByUser(userId);
                        Integer senderId;
                        for (Chat chat : userChat) {
                                senderId = chat.getUser1().getId().equals(userId) ? chat.getUser2().getId()
                                                : chat.getUser1().getId();
                                sendToSocket(chat.getId(), -1, MessageStatus.RECEIVED,
                                                senderId);
                        }
                } catch (Exception e) {

                        throw new CustomException(e.getMessage(), HttpStatus.NOT_FOUND);
                }
        }

        /******************************************************************************* */

        private void sendToSocket(Integer chatId, Integer messageId, MessageStatus messageStatus, Integer senderId) {
                UpdateMessageStatusDto notify = new UpdateMessageStatusDto(
                                chatId, messageId, messageStatus);

                messagingTemplate.convertAndSend("/topic/messages/" + senderId,
                                notify);

        }

        /******************************************************************************* */

}
