package com.example.chat.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chat.dtos.ChatDto;
import com.example.chat.dtos.MessageDto;
import com.example.chat.exception.CustomException;
import com.example.chat.mappers.ChatMapper;
import com.example.chat.mappers.TextMessageMapper;
import com.example.chat.models.Chat;
import com.example.chat.models.LocalUser;
import com.example.chat.models.TextMessage;
import com.example.chat.repositories.ChatRepository;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.repositories.TextMessageRepository;
import com.example.chat.security.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service

public class ChatService {

    private ChatMapper chatMapper;
    private TextMessageMapper textMessageMapper;
    private ChatRepository chatRepository;
    private LocalUserRepository localUserRepository;
    private TokenUtil tokenUtil;
    private TextMessageRepository textMessageRepository;

    public ChatService(ChatMapper chatMapper, TextMessageMapper textMessageMapper,
            ChatRepository chatRepository, LocalUserRepository localUserRepository,
            TokenUtil tokenUtil,
            TextMessageRepository textMessageRepository) {

        this.textMessageRepository = textMessageRepository;
        this.chatMapper = chatMapper;
        this.textMessageMapper = textMessageMapper;
        this.tokenUtil = tokenUtil;

        this.chatRepository = chatRepository;
        this.localUserRepository = localUserRepository;

    }

    /**************************************************************************************** */
    @Transactional
    public Chat createChatWithUser(Integer user2Id) throws IOException, TimeoutException {

        LocalUser user1 = localUserRepository.findById(tokenUtil.getUserId())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (user1.getId() == user2Id) {
            throw new CustomException("You can't chat with yourself", HttpStatus.BAD_REQUEST);
        }
        LocalUser user2 = localUserRepository.findById(user2Id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Chat chat = chatRepository.findByUser1AndUser2(user1.getId(), user2Id).orElse(null);

        if (chat != null) {
            return chat;
        }
        chat = new Chat(user1, user2, LocalDateTime.now());
        chatRepository.save(chat);

        return chat;
    }

    /**
     * @throws JsonProcessingException
     * @throws JsonMappingException
     *                                 **************************************************************************************
     */

    public List<ChatDto> getUserChats() throws JsonMappingException, JsonProcessingException {

        LocalUser user = localUserRepository.findById(tokenUtil.getUserId())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<ChatDto> chats = chatRepository.findChatByUser(user.getId()).stream()
                .map(
                        chat -> {
                            // textMessageRepository.setChatMessagesToRecieved(chat.getId(), user.getId());
                            MessageDto messageDto = getLastMessage(chat.getId());
                            Long numberOfUnreadMessages = textMessageRepository.getNumberOfUnreadMessage(chat.getId(),
                                    user.getId());

                            ChatDto chatDto = chatMapper.toDto(chat, user);
                            chatDto.setLastMessage(messageDto);
                            chatDto.setNumberOfUreadMessages(numberOfUnreadMessages);
                            return chatDto;
                        })
                .toList();

        // List<ChatDto> chatDtos = new ArrayList<>();

        // for (Chat c : chats) {

        // textMessageRepository.setChatMessagesToRecieved(c.getId(), user.getId());
        // MessageDto messageDto = getLastMessage(c.getId());
        // Long numberOfUnreadMessages =
        // textMessageRepository.getNumberOfUnreadMessage(c.getId(), user.getId());

        // ChatDto chatDto = chatMapper.toDto(c, user);
        // chatDto.setLastMessage(messageDto);
        // chatDto.setNumberOfUreadMessages(numberOfUnreadMessages);

        // chatDtos.add(chatDto);

        // }
        return chats;

    }

    /***************************************************************************************/
    private MessageDto getLastMessage(Integer chatId) {
        List<TextMessage> textMessages = textMessageRepository.getMessagesByChatId(chatId);
        if (textMessages.isEmpty())
            return null;
        return textMessageMapper.toDto(textMessages.getLast(), chatId);
    }

    /**************************************************************************************** */
    // public Chat getChat(Integer chatId) {
    // Chat chat = chatRepository.findById(chatId).orElseThrow(
    // () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));
    // return chat;
    // }

    /**************************************************************************************** */
    public ChatDto getChat(Integer chatId) {
        try {
            Chat chat = chatRepository.findById(chatId).orElseThrow(
                    () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));
            LocalUser user = localUserRepository.findById(tokenUtil.getUserId())
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

            return chatMapper.toDto(chat, user);
        } catch (Exception e) {
            throw new CustomException("Chat not found", HttpStatus.NOT_FOUND);
        }

    }

    /*************************************************************************************************************/

    public ChatDto searchForUserChatByUserId(Integer userId) throws IOException, TimeoutException {
        Chat chat = createChatWithUser(userId);
        LocalUser user = localUserRepository.findById(tokenUtil.getUserId())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return chatMapper.toDto(chat, user);
    }
}
