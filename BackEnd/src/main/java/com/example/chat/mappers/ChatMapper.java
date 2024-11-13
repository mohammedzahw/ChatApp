package com.example.chat.mappers;

import org.springframework.stereotype.Component;

import com.example.chat.dtos.ChatDto;
import com.example.chat.models.Chat;
import com.example.chat.models.LocalUser;

@Component
public class ChatMapper {

    /********************************************************************************* */

    public ChatDto toDto(Chat chat, LocalUser user) {
        if (chat == null) {
            return null;
        }

        ChatDto chatDto = new ChatDto();
        LocalUser sender = chat.getUser1().getId() == user.getId() ? chat.getUser2()
                : chat.getUser1();

        chatDto.setName(sender.getName());

        chatDto.setImageUrl(sender.getImageUrl());
        chatDto.setId(chat.getId());

        chatDto.setLastUpdated(chat.getLastUpdated());

        return chatDto;
    }

    /********************************************************************************* */

}
