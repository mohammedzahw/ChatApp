package com.example.chat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.models.LocalUser;

import jakarta.transaction.Transactional;

public interface LocalUserRepository extends JpaRepository<LocalUser, Integer> {

    Optional<LocalUser> findByEmail(String email);

    Optional<LocalUser> findByEmailAndPassword(String email, String password);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO local_user_listend_queues (user_id, queue_id) VALUES (:userId, :queueId)", nativeQuery = true)
    void addQueue(Integer userId, Integer queueId);

    @Query("SELECT l FROM LocalUser l WHERE  l.email LIKE %:searchText%   AND l.id != :userId")
    List<LocalUser> searchUser(String searchText, Integer userId);

}
