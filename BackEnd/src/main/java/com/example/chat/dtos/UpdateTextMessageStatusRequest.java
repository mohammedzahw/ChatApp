package com.example.chat.dtos;

import java.time.LocalDateTime;

import com.example.chat.models.MessageStatus;

import lombok.Data;

@Data
public class UpdateTextMessageStatusRequest {

    private Integer messageId;
    private MessageStatus status;
    private LocalDateTime time;

}
