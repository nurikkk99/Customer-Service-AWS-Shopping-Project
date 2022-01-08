package com.epam.customerservice.config.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.GetImageDto;
import com.epam.customerservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collection;
import java.util.List;
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
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ProductControllerImageRequestsTest {

    public static String API_PATH = "/api/products/";

    private GetImageDto firstImage;
    private GetImageDto secondImage;

    @Autowired
    JdbcDatabaseContainer jdbcDatabaseContainer;
    
    @Autowired
    ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void prepareData() {
        firstImage = new GetImageDto();
        firstImage.setImageId(1L);
        firstImage.setGoodId(1L);
        firstImage.setImageURI("testUri");

        secondImage = new GetImageDto();
        secondImage.setImageId(2L);
        secondImage.setGoodId(1L);
        secondImage.setImageURI("secondTestUri");

        productService.saveImage(firstImage);
        productService.saveImage(secondImage);
    }

    @After
    public void dropData() {
        productService.deleteAll();
    }

    @Test
    public void getAllImagesByGoodIdTest() throws Exception {
        final Collection<GetImageDto> expectedCollection = List.of(firstImage, secondImage);
        assertNotNull(expectedCollection);
        assertFalse(expectedCollection.isEmpty());
        assertEquals(firstImage.getGoodId(), secondImage.getGoodId());
        final String urlTemplate = API_PATH + firstImage.getGoodId() + "/image";

        final String contentAsString = mockMvc.perform(get(urlTemplate)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(expectedCollection.size())))
                .andReturn().getResponse()
                .getContentAsString();

        Collection<GetImageDto> resultCollection = objectMapper.readValue(
                contentAsString,
                TypeFactory.defaultInstance().constructCollectionType(Collection.class, GetImageDto.class)
        );
        assertTrue(resultCollection.containsAll(expectedCollection));
    }

    @Test
    public void getImageByImageIdTest() throws Exception {
        final GetImageDto expectedImage = firstImage;
        final String urlTemplate = API_PATH + firstImage.getGoodId() + "/image/" + firstImage.getImageId();

        mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.imageId").isNotEmpty())
                .andExpect(jsonPath("$.goodId", is(firstImage.getGoodId().intValue())))
                .andExpect(jsonPath("$.imageURI", is(firstImage.getImageURI())));
    }
}
