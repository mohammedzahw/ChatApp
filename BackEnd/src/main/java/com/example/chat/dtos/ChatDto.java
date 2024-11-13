package com.example.chat.dtos;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatDto {
    private Integer id;

    private String name;

    private String imageUrl;
    private LocalDateTime lastUpdated;

    private Long numberOfUreadMessages;

    private MessageDto lastMessage;

    public ChatDto(Integer id, String name, String imageUrl, LocalDateTime lastUpdated,
            Long numberOfUreadMessages, MessageDto lastMessage) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.lastUpdated = lastUpdated;
        this.numberOfUreadMessages = numberOfUreadMessages;
        this.lastMessage = lastMessage;
    }

}
