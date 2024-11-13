package com.example.chat.models;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Chat {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;
        private LocalDateTime lastUpdated;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH
        })
        @JoinColumn(name = "user1_id")
        @ToStringExclude
        @JsonIgnore
        private LocalUser user1;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH
        })
        @JoinColumn(name = "user2_id")
        @ToStringExclude
        @JsonIgnore
        private LocalUser user2;

        @ToStringExclude
        @JsonIgnore
        @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<TextMessage> messages;

        public Chat(LocalUser user1, LocalUser user2, LocalDateTime lastUpdated) {
                this.user1 = user1;
                this.user2 = user2;
                this.lastUpdated = lastUpdated;
        }

}
