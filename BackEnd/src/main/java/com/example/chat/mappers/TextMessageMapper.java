package com.example.chat.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.dtos.MessageDto;
import com.example.chat.dtos.SendTextMessageRequest;
import com.example.chat.models.Chat;
import com.example.chat.models.LocalUser;
import com.example.chat.models.MessageStatus;
import com.example.chat.models.TextMessage;

@Component

public class TextMessageMapper {

    public MessageDto toDto(TextMessage textMessage, Integer chatId) {
        if (textMessage == null) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setContent(textMessage.getContent());
        if (textMessage.getId() != null) {
            messageDto.setId(textMessage.getId());
        }
        messageDto.setChatId(chatId);
        messageDto.setReceiveDateTime(textMessage.getReceiveDateTime());
        messageDto.setReceiverId(textMessage.getReceiver().getId());
        messageDto.setSendDateTime(textMessage.getSendDateTime());
        messageDto.setSenderId(textMessage.getSender().getId());
        messageDto.setStatus(textMessage.getStatus());

        return messageDto;
    }

    /********************************************************************************************* */
    public TextMessage toEntity(SendTextMessageRequest sendTextMessageRequest, Chat chat, LocalUser sender,
            LocalUser receiver) {
        if (sendTextMessageRequest == null) {
            return null;
        }

        TextMessage textMessage = new TextMessage();
        textMessage.setChat(chat);

        textMessage.setContent(sendTextMessageRequest.getText());
        textMessage.setReceiver(receiver);

        textMessage.setSendDateTime(LocalDateTime.now());
        textMessage.setSender(sender);
        textMessage.setStatus(MessageStatus.SENT);

        return textMessage;
    }

    /********************************************************************************************* */

    public List<MessageDto> toDtoList(List<TextMessage> messageChats, Integer chatId) {
        if (messageChats == null) {
            return null;
        }

        List<MessageDto> list = new ArrayList<MessageDto>(messageChats.size());
        for (TextMessage textMessage : messageChats) {
            list.add(toDto(textMessage, chatId));
        }

        return list;
    }

}
