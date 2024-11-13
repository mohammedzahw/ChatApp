package com.example.chat.dtos;

import java.time.LocalDateTime;

import com.example.chat.models.MessageStatus;

import lombok.Data;

@Data
public class MessageDto {
    private Integer id;
    private Integer chatId;
    private String content;
    private MessageStatus status;
    private LocalDateTime sendDateTime;
    private Integer senderId;
    private Integer receiverId;
    private LocalDateTime receiveDateTime;

}
