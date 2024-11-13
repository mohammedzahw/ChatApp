package com.example.chat.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendTextMessageRequest {
    private String text;
    private Integer chatId;
    // private Integer parentMessageId;
}
