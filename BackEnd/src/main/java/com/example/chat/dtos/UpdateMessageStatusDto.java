package com.example.chat.dtos;

import com.example.chat.models.MessageStatus;

import lombok.Data;

@Data
public class UpdateMessageStatusDto {

    private Integer chatId;
    private Integer messageId;
    private MessageStatus messageStatus;

    public UpdateMessageStatusDto(Integer chatId, Integer messageId, MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
        this.messageId = messageId;
        this.chatId = chatId;
    }

}
