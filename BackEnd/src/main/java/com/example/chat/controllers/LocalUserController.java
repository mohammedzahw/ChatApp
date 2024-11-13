package com.example.chat.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.services.LocalUserService;
import com.example.chat.shared.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LocalUserController {
    private final LocalUserService localUserService;

    /********************************************************************************************/

    @GetMapping("/online/{userId}")
    public Response makeUserOnline(@PathVariable("userId") String userId)
            throws Exception {
        localUserService.makeUserOnline(userId);
        return new Response(HttpStatus.OK, null, "User made online");

    }

    /********************************************************************************************/

    @GetMapping("/offline/{userId}")
    public Response makeUserOffline(@PathVariable("userId") String userId) {
        localUserService.makeUserOffline(userId);
        return new Response(HttpStatus.OK, null, "User made ofline");

    }

    /********************************************************************************************/
    @GetMapping("/search/{searchText}")
    public Response searchForUser(@PathVariable String searchText) {
        return new Response(HttpStatus.OK, localUserService.searchForUser(searchText), "User fetched");
    }

    /*********************************************************************************************/

    @GetMapping("/current-user")
    public Response getUser() {
        return new Response(HttpStatus.OK, localUserService.getUser(), "User fetched");
    }

}
