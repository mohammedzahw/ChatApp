package com.example.chat.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.dtos.LocalUserDto;
import com.example.chat.dtos.SignUpRequest;
import com.example.chat.models.LocalUser;

@Component

public class LocalUserMapper {



    public LocalUser toEntity(SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            return null;
        }

        LocalUser localUser = new LocalUser();
        localUser.setAbout(signUpRequest.getAbout());
        localUser.setEmail(signUpRequest.getEmail());
        localUser.setName(signUpRequest.getName());
        localUser.setPassword(signUpRequest.getPassword());

        localUser.setActive(false);
        localUser.setOnline(false);



        return localUser;
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
