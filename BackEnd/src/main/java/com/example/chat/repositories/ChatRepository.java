package com.example.chat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.chat.models.Chat;

@Repository
@SuppressWarnings("null")
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("SELECT c FROM Chat c WHERE c.id = :id")
    Optional<Chat> findById(Integer id);

    @Query("SELECT c FROM Chat c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) OR (c.user1.id = :user2Id AND c.user2.id = :user1Id) ORDER BY c.lastUpdated DESC")
    Optional<Chat> findByUser1AndUser2(Integer user1Id, Integer user2Id);

    @Query("SELECT c FROM Chat c WHERE  (c.user1.id = :userId ) OR (c.user2.id = :userId)  ORDER BY c.lastUpdated DESC")
    List<Chat> findChatByUser(Integer userId);

    @Query("SELECT c FROM Chat c JOIN c.messages m WHERE m.id = :messageId")
    Optional<Chat> getChatByMessageId(Integer messageId);

}