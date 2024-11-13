package com.example.chat.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.dtos.LocalUserDto;
import com.example.chat.dtos.SignUpRequest;
import com.example.chat.models.LocalUser;

@Component

public class LocalUserMapper {

    public LocalUser toEntity(LocalUserDto userDto) {
        if (userDto == null) {
            return null;
        }

        LocalUser.LocalUserBuilder localUser = LocalUser.builder();

        localUser.about(userDto.getAbout());

        localUser.id(userDto.getId());

        localUser.name(userDto.getName());

        return localUser.build();
    }

    /********************************************************************************************* */

    public LocalUser toEntity(SignUpRequest signUpRequestDto) {
        if (signUpRequestDto == null) {
            return null;
        }

        LocalUser.LocalUserBuilder localUser = LocalUser.builder();

        localUser.about(signUpRequestDto.getAbout());
        localUser.email(signUpRequestDto.getEmail());
        localUser.name(signUpRequestDto.getName());
        localUser.password(signUpRequestDto.getPassword());

        return localUser.build();
    }

    /********************************************************************************************* */

    public LocalUserDto toDto(LocalUser user) {
        if (user == null) {
            return null;
        }

        LocalUserDto localUserDto = new LocalUserDto();

        localUserDto.setAbout(user.getAbout());
        localUserDto.setId(user.getId());
        localUserDto.setEmail(user.getEmail());
        localUserDto.setOnline(user.getOnline());
        localUserDto.setImageUrl(user.getImageUrl());
        localUserDto.setName(user.getName());

        return localUserDto;
    }

    /*************************************************************************************************/

    public List<LocalUserDto> toDtoList(List<LocalUser> customers) {
        if (customers == null) {
            return null;
        }

        List<LocalUserDto> list = new ArrayList<LocalUserDto>(customers.size());
        for (LocalUser localUser : customers) {
            list.add(toDto(localUser));
        }

        return list;
    }

    /************************************************************************************************/

}
