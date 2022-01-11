package com.epam.customerservice.config.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.service.RabbitMqListener;
import com.epam.customerservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    public static String API_PATH = "/api/users/";

    private Long sessionId;
    private UserDto savedUserDto;

    @MockBean
    RabbitMqListener rabbitMqListener;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void prepareData() throws ParseException {
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
    public void createUserTest() throws Exception {
        Long sessionId = userService.createUser();

        String contentAsString = mockMvc.perform(post(API_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse()
                .getContentAsString();
        assertFalse(contentAsString.isEmpty());

    }

    @Test
    public void deleteUserTest() throws Exception {
        final String urlTemplate = API_PATH + sessionId;
        mockMvc.perform(delete(urlTemplate)).andExpect(status().isOk());
    }

    @Test
    public void getUserInfoTest() throws Exception {
        final String urlTemplate = API_PATH + "/userInfo/" + savedUserDto.getId();

        final String contentAsString = mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse()
                .getContentAsString();
        UserDto actualDto = objectMapper.readValue(contentAsString, UserDto.class);
        assertThat(actualDto, is(savedUserDto));
    }

    @Test
    public void registerUserInfoTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(100L);
        userDto.setName("Aleksei");
        userDto.setSurname("Chebatov");
        userDto.setPhoneNumber(893364456634L);

        byte[] content = objectMapper.writeValueAsBytes(userDto);
        final String urlTemplate = API_PATH + "/userInfo";

        mockMvc.perform(post(urlTemplate).contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.surname").value(userDto.getSurname()))
                .andExpect(jsonPath("$.phoneNumber").value(userDto.getPhoneNumber()));

    }

    @Test
    public void deleteUserInfoTest() throws Exception {
        userService.deleteUser(sessionId);
        final String urlTemplate = API_PATH + "/userInfo/" + savedUserDto.getId();
        mockMvc.perform(delete(urlTemplate)).andExpect(status().isOk());
    }

    @Test
    public void putUserInfoToSessionIdTest() throws Exception {
        Long newSessionId = userService.createUser();
        final String urlTemplate = API_PATH + newSessionId + "/userInfo/" + savedUserDto.getId();
        mockMvc.perform(put(urlTemplate)).andExpect(status().isOk());
    }

    @Test
    public void getUserInfoBySessionIdTest() throws Exception {
        final String urlTemplate = API_PATH + sessionId + "/userInfo/";

        final String contentAsString = mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse()
                .getContentAsString();
        UserDto actualDto = objectMapper.readValue(contentAsString, UserDto.class);
        assertThat(actualDto, is(savedUserDto));
    }
}
