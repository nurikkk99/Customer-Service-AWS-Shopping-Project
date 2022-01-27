package com.epam.customerservice.controller;

import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Long createUser() {
        logger.info("Creating new session id");
        return userService.createUser();
    }

    @DeleteMapping("/{sessionId}")
    public void deleteUser(@PathVariable("sessionId") long sessionId) {
        logger.info("Deleting user with session id = {}", sessionId );
        userService.deleteUser(sessionId);
    }

    @GetMapping("/userInfo/{userInfoId}")
    public UserDto getUserInfo(@PathVariable("userInfoId") long userInfoId) {
        return userService.getUser(userInfoId);
    }

    @PostMapping("/userInfo")
    public UserDto registerUserInfo(@RequestBody UserDto userDto) {
        logger.info("Register user with name = {}, surname = {}, address = {}, phone number = {}", userDto.getName(),
                userDto.getSurname(), userDto.getAddress(), userDto.getPhoneNumber());
        return userService.registerUser(userDto);
    }

    @DeleteMapping("/userInfo/{userInfoId}")
    public void deleteUserInfo(@PathVariable long userInfoId) {
        logger.info("Deleting user info with id = {}", userInfoId);
        userService.deleteUserInfo(userInfoId);
    }

    @PutMapping("/{sessionId}/userInfo/{userInfoId}")
    public void putUserInfoToSessionID(@PathVariable("sessionId") long sessionId, @PathVariable("userInfoId") long userInfoId) {
        logger.info("Putting user info with id = {} into session with id = {}", userInfoId, sessionId);
        userService.putRegisteredUserToUser(sessionId, userInfoId);
    }

    @GetMapping("/{sessionId}/userInfo")
    public UserDto getUserInfoBySessionId(@PathVariable("sessionId") long sessionId) {
        return userService.getRegisteredUserBySessionID(sessionId);
    }
}
