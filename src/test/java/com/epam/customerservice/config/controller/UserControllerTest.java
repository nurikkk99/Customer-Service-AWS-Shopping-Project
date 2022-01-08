package com.epam.customerservice.config.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.GoodsType;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.UserDto;
import com.epam.customerservice.service.ProductService;
import com.epam.customerservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    public static String API_PATH = "/api/users/";

    private Long sessionId;
    private UserDto savedUserDto;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Before
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

    @After
    public void dropData() {
        userService.deleteAllSessionIds();
        userService.deleteAllUserInfo();
    }

    @Test
    public void createUserTest() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setId(100L);
//        userDto.setName("Aleksei");
//        userDto.setSurname("Chebatov");
//        userDto.setPhoneNumber(893364456634L);
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
