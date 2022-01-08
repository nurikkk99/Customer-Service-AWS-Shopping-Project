package com.epam.customerservice.config.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.exception.EntityNotFoundException;
import com.epam.customerservice.repository.RegisteredUserRepository;
import com.epam.customerservice.service.UserService;
import org.apache.catalina.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@RunWith(SpringRunner.class)
public class UserServiceTest {

    private Long sessionId;
    private UserDto savedUserDto;

    @Autowired
    private JdbcDatabaseContainer jdbcDatabaseContainer;

    @Autowired
    private UserService userService;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Before
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

    @After
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
