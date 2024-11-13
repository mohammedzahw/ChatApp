package com.example.chat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.models.TextMessage;

import jakarta.transaction.Transactional;

public interface TextMessageRepository extends JpaRepository<TextMessage, Integer> {

    // @Query("SELECT m.receiver FROM TextMessage m WHERE m.id = :MessageChatId")
    // Optional<LocalUser> getReceiverByMessageChatId(Integer MessageChatId);

    // get last message
    @Query("SELECT m FROM TextMessage m WHERE m.chat.id = :chatId ORDER BY m.sendDateTime ASC")
    List<TextMessage> getMessagesByChatId(Integer chatId);

    @Query("SELECT count(m) FROM TextMessage m WHERE (m.chat.id = :chatId AND m.status != 'READ' And m.sender.id != :userId)")
    Long getNumberOfUnreadMessage(Integer chatId, Integer userId);

    @Query("SELECT m.chat.id FROM TextMessage m WHERE m.id = :messageId")
    Integer getChatId(Integer messageId);

    @Modifying
    @Transactional
    @Query("UPDATE TextMessage m SET m.status = 'READ' , m.receiveDateTime = CURRENT_TIMESTAMP WHERE ( m.chat.id = :chatId  And m.sender.id != :userId And m.status !='READ' )")
    void setChatMessagesToRead(Integer chatId, Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE TextMessage m SET m.status='RECEIVED', m.receiveDateTime = CURRENT_TIMESTAMP WHERE (m.chat.id = :chatId  And m.sender.id != :userId And m.status ='SENT')")
    void setChatMessagesToRecieved(Integer chatId, Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE TextMessage m SET m.status = 'RECEIVED', m.receiveDateTime = CURRENT_TIMESTAMP WHERE (m.receiver.id = :id And m.status = 'SENT')")
    void setUserMessagesToReceive(Integer id);

}
