package com.example.chat.controllers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.dtos.SendTextMessageRequest;
import com.example.chat.dtos.UpdateTextMessageStatusRequest;
import com.example.chat.services.TextMessageService;
import com.example.chat.shared.Response;

@RestController
@RequestMapping("/api")
public class TextMessageController {
    private TextMessageService textMessageService;

    public TextMessageController(TextMessageService textMessageService) {
        this.textMessageService = textMessageService;
    }

    /************************************************************************************************/

    @PostMapping("/send-message/text")
    public Response sendTextMessage(@RequestBody SendTextMessageRequest message)

            throws IOException, TimeoutException {
        try {

            return new Response(HttpStatus.CREATED, textMessageService.sendTextMessage(message), "Message sent");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    /************************************************************************************************/
    @PostMapping("/text-message/update-status")
    public Response updateTextMessageStatus(@RequestBody UpdateTextMessageStatusRequest message)

            throws IOException, TimeoutException {
        try {
            // System.out.println("message : " + message);

            return new Response(HttpStatus.CREATED, textMessageService.updateTextMessageStatus(message),
                    "Message updated");
        } catch (Exception e) {
            return new Response(HttpStatus.NOT_FOUND, null, "Message not found");
        }
    }

    /************************************************************************************************/
    @GetMapping("/messages/{chatId}")
    public Response getChatMessages(@PathVariable("chatId") Integer chatId) {
        try {
            return new Response(HttpStatus.OK,
                    textMessageService.getMessagesByChatId(chatId), "Messages fetched");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

    }

    /*************************************************************************************************/

    @GetMapping("/chat/read-messages/{chatId}")
    public Response setChatMessagesToRead(@PathVariable("chatId") Integer chatId) {
        // System.out.println("chatId : " + chatId);
        try {
            textMessageService.setChatMessagesToRead(chatId);

            return new Response(HttpStatus.OK, null, "Messages readed");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

    }

    /*************************************************************************************************/
    @GetMapping("/messages/receive")
    public Response setUserMessagesToReceive() {
        // System.out.println("chatId : " + chatId);
        try {
            textMessageService.setUserMessagesToReceive();

            return new Response(HttpStatus.OK, null, "Messages readed");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

    }

}
