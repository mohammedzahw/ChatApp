package com.example.chat.models;

import lombok.Getter;

@Getter
public enum MessageStatus {
    SENT,
    RECEIVED,
    READ;
}