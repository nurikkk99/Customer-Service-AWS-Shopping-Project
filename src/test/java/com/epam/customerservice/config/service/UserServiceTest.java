package com.epam.customerservice.config.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.exception.EntityNotFoundException;
import com.epam.customerservice.repository.RegisteredUserRepository;
import com.epam.customerservice.service.RabbitMqListener;
import com.epam.customerservice.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@ActiveProfiles("test")
public class UserServiceTest {

    private Long sessionId;
    private UserDto savedUserDto;

    @MockBean
    RabbitMqListener rabbitMqListener;

    @Autowired
    private JdbcDatabaseContainer jdbcDatabaseContainer;

    @Autowired
    private UserService userService;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @BeforeEach
    public void prepareData() {
        sessionId = userService.createUser();

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Ivan");
        userDto.setSurname("Smirnov");
        userDto.setPhoneNumber(89336756634L);
        savedUserDto = userService.registerUser(userDto);

        userService.putRegisteredUserToUser(sessionId, savedUserDto.getId());
    }

    @AfterEach
    public void dropData() {
        userService.deleteAllSessionIds();
        userService.deleteAllUserInfo();
    }

    @Test
    public void getUserInfoBySessionIdTest() {
        UserDto userDto = userService.getRegisteredUserBySessionID(sessionId.intValue());
        assertEquals(savedUserDto, userDto);
    }

    @Test
    public void getUserInfoBySessionIdShouldThrowExceptionIfUserInfoDoesNotExist() {
        Long sessionIdWIthEmptyUserInfo = sessionId + 1;
        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getRegisteredUserBySessionID(sessionIdWIthEmptyUserInfo)
        );
    }

    @Test
    public void getUserInfoByIdTest() {
        UserDto userDto = userService.getUser(savedUserDto.getId());
        assertEquals(savedUserDto, userDto);
    }

    @Test
    public void deleteUserTest() {
        userService.deleteUser(sessionId);
        assertFalse(userService.checkSessionIdIsExists(sessionId));
    }

    @Test
    public void deleteUserInfoByUserInfoIdTest() {
        userService.deleteUser(sessionId);
        userService.deleteUserInfo(savedUserDto.getId());
        assertThrows(Exception.class, ()->userService.getUser(savedUserDto.getId()).getId());
    }

}
