package com.example.chat.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.chat.dtos.LocalUserDto;
import com.example.chat.exception.CustomException;
import com.example.chat.mappers.LocalUserMapper;
import com.example.chat.models.LocalUser;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.security.TokenUtil;

@Service

public class LocalUserService {
    private LocalUserMapper localUserMapper;
    private LocalUserRepository localUserRepository;

    private TokenUtil tokenUtil;

    public LocalUserService(LocalUserMapper localUserMapper, LocalUserRepository localUserRepository,
            TokenUtil tokenUtil) {

        this.localUserMapper = localUserMapper;
        this.localUserRepository = localUserRepository;

        this.tokenUtil = tokenUtil;
    }

    /*******************************************************************************************/

    public List<LocalUserDto> searchForUser(String searchText) {

        List<LocalUser> users = localUserRepository.searchUser(searchText, tokenUtil.getUserId());
        return localUserMapper.toDtoList(users);

    }

    /*******************************************************************************************/
    public void makeUserOnline(String userId) throws Exception {
        LocalUser user = localUserRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        user.setOnline(true);
        localUserRepository.save(user);

    }

    /*******************************************************************************************/
    public void makeUserOffline(String userId) {
        LocalUser user = localUserRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        user.setOnline(false);
        localUserRepository.save(user);
    }

    /*******************************************************************************************/

    public LocalUserDto getUser() {

        LocalUser user = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return localUserMapper.toDto(user);

    }

    /*******************************************************************************************/
}
