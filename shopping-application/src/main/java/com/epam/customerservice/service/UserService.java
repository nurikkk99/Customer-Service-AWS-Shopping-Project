package com.epam.customerservice.service;

import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.entity.RegisteredUserEntity;
import com.epam.customerservice.entity.UserEntity;
import com.epam.customerservice.exception.EntityNotFoundException;
import com.epam.customerservice.repository.RegisteredUserRepository;
import com.epam.customerservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    private RegisteredUserRepository registeredUserRepository;
    private UserDto userDto;

    public UserService(UserRepository userRepository, RegisteredUserRepository registeredUserRepository) {
        this.userRepository = userRepository;
        this.registeredUserRepository = registeredUserRepository;
        this.userDto = new UserDto();
    }

    public Long createUser() {
        UserEntity userEntity = new UserEntity();
        logger.info("Creating new session id and saving in database");
        UserEntity savedUserEntity = userRepository.save(userEntity);
        logger.info("Session id = {} was created", userEntity.getSessionID());
        return savedUserEntity.getSessionID();
    }

    public void putRegisteredUserToUser(long sessionId, long registeredUserID) {
        UserEntity userEntity = userRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session id does not exist"));
        RegisteredUserEntity registeredUser = registeredUserRepository.findById(registeredUserID)
                .orElseThrow(() -> new EntityNotFoundException("Registered user id does not exist"));
        userEntity.setRegisteredUser(registeredUser);
        logger.info("Setting user info = {} into session id = {}", userDto.entityToDto(registeredUser), sessionId);
        userRepository.save(userEntity);
    }

    public UserDto getRegisteredUserBySessionID(long sessionId) {
        UserEntity userEntity = userRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session id does not exist"));
        if (userEntity.getRegisteredUser() != null) {
            RegisteredUserEntity registeredUser = registeredUserRepository.findById(
                            userEntity.getRegisteredUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Registered user id does not exist"));
            return userDto.entityToDto(registeredUser);
        }
        return null;
    }

    public Boolean checkSessionIdIsExists(Long sessionId) {
        return userRepository.existsById(sessionId);
    }

    public UserDto registerUser(UserDto userDto) {
        logger.info("Register user with name = {}, surname = {}, address = {}, phone number = {}", userDto.getName(),
                userDto.getSurname(), userDto.getAddress(), userDto.getPhoneNumber());
        UserDto savedUserDto = userDto.entityToDto(registeredUserRepository.save(userDto.dtoToEntity()));
        logger.info("User registered with id = {}", savedUserDto.getId());
        return savedUserDto;
    }

    public UserDto getUser(long id) {
        RegisteredUserEntity registeredUser = registeredUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registered user id does not exist"));
        return userDto.entityToDto(registeredUser);
    }

    public void deleteUserInfo(long id) {
        registeredUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registered user id does not exist"));
        logger.info("Deleting user info with id = {}", id);
        registeredUserRepository.deleteById(id);
    }

    public void deleteUser(long sessionId) {
        logger.info("Deleting user with session id = {}", sessionId);
        userRepository.deleteById(sessionId);
    }

    public void deleteAllSessionIds() {
        logger.info("Deleting all session ids in database");
        userRepository.deleteAll();
    }

    public void deleteAllUserInfo() {
        logger.info("Deleting all user info in database");
        registeredUserRepository.deleteAll();
    }
}
