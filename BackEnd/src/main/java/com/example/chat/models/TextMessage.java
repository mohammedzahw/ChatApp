package com.example.chat.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TextMessage {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;
        private String content;
        private LocalDateTime sendDateTime;
        private LocalDateTime receiveDateTime;
        @Enumerated(EnumType.STRING)
        private MessageStatus status;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH

        })

        // @JsonIgnore
        @JoinColumn(name = "sender_id")
        private LocalUser sender;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH

        })

        // @JsonIgnore
        @JoinColumn(name = "receiver_id")
        private LocalUser receiver;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH

        })
        @ToString.Exclude
        @JsonIgnore
        @JoinColumn(name = "chat_id")
        private Chat chat;

}
