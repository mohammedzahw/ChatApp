package com.example.chat.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalUserDto {
    private Integer id;
    private String name;
    private String about;
    private String imageUrl;
    private String email;
    private Boolean online;

}
