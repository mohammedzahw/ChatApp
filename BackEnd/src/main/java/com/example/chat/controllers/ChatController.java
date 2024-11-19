package com.example.chat.controllers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.services.ChatService;
import com.example.chat.shared.Response;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j

public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /********************************************************************************************************/

    @GetMapping("/{chatId}")
    public Response getChat(@PathVariable("chatId") Integer chatId) throws IOException, TimeoutException {

        return new Response(HttpStatus.OK, chatService.getChat(chatId), "Chat fetched");
    }

    /**********************************************************************************************************/

    @GetMapping("/user-chats")
    public ResponseEntity<?> getUserChats() throws IOException, TimeoutException {
        return new ResponseEntity<>(chatService.getUserChats(), HttpStatus.OK);
    }

    /*********************************************************************************************************/
    @DeleteMapping("delete-messages/{chatId}")
    public Response deleteChatMessages(@PathVariable("chatId") Integer chatId) {
        try {
            chatService.deleteChatMessages(chatId);
            return new Response(HttpStatus.OK, null, "chat deleted");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    /*********************************************************************************************************/
    @DeleteMapping("delete/{chatId}")
    public Response deleteChat(@PathVariable("chatId") Integer chatId) {
        try {
            chatService.deleteChat(chatId);
            return new Response(HttpStatus.OK, null, "chat deleted");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    /*********************************************************************************************************/

    @GetMapping("/search-chat/{userId}")
    public Response searchForUserChatByUserId(@PathVariable("userId") Integer userId) throws Exception {
        try {
            return new Response(HttpStatus.OK, chatService.searchForUserChatByUserId(userId), "Chats fetched");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }
}
