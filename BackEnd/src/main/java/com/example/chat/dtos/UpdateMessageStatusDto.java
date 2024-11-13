package com.example.chat.dtos;

import com.example.chat.models.MessageStatus;

import lombok.Data;

@Data
public class UpdateMessageStatusDto {

    private Integer chatId;
    private MessageStatus messageStatus;

    public UpdateMessageStatusDto(Integer chatId, MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
        this.chatId = chatId;
    }

}
