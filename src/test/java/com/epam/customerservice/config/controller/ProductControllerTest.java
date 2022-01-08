package com.epam.customerservice.config.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.customerservice.config.config.ElasticSearchTestContainer;
import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.GoodsType;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.SearchAndFilterRequestDto;
import com.epam.customerservice.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.search.sort.SortOrder;
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

@SpringBootTest(classes = {ElasticSearchTestContainer.class, TestContainerConfig.class})
@Testcontainers
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    public static String API_PATH = "/api/products/";

    private ProductDto savedProductDto1;
    private ProductDto savedProductDto2;

    @Autowired
    private TestContainerConfig testContainerConfig;

    @Autowired
    private ElasticsearchContainer elasticsearchContainer;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void prepareData() throws ParseException {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Air Force");
        productDto.setManufacturer("Nike");
        productDto.setPrice(BigDecimal.valueOf(4000));
        productDto.setType(GoodsType.Sneakers);
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String string1 = "2021-12-30T13:00:00.000-0700";
        productDto.setReleaseDateTime(df1.parse(string1));
        savedProductDto1 = productService.save(productDto);

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setName("Air");
        productDto2.setManufacturer("Nike");
        productDto2.setPrice(BigDecimal.valueOf(7000));
        productDto2.setType(GoodsType.Sneakers);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String string2 = "2021-12-27T13:00:00.000-0700";
        productDto2.setReleaseDateTime(df2.parse(string2));
        savedProductDto2 = productService.save(productDto2);

        ProductDto productDto3 = new ProductDto();
        productDto3.setId(3L);
        productDto3.setName("Stan Smith");
        productDto3.setManufacturer("Adidas");
        productDto3.setPrice(BigDecimal.valueOf(5000));
        productDto3.setType(GoodsType.Sneakers);
        DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String string3 = "2021-12-29T13:00:00.000-0700";
        productDto3.setReleaseDateTime(df3.parse(string3));
        productService.save(productDto3);
    }

    @After
    public void dropData() {
        productService.deleteAll();
    }

    @Test
    public void findOneTest() throws Exception {
        final ProductDto expectedDto = productService.findById(savedProductDto1.getId());
        assertNotNull("Dto is null", expectedDto);

        final String contentAsString = mockMvc.perform(get(API_PATH + expectedDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse()
                .getContentAsString();
        ProductDto actualDto = objectMapper.readValue(contentAsString, ProductDto.class);
        assertThat(actualDto, is(expectedDto));
    }

    @Test
    public void searchAndFilterTest() throws Exception {
        SearchAndFilterRequestDto requestDto = new SearchAndFilterRequestDto();
        List<String> searchFields = new ArrayList<>();
        searchFields.add("name");
        requestDto.setSearchFields(searchFields);
        requestDto.setSearchTerm("Air");
        requestDto.setType("Sneakers");
        requestDto.setManufacturer("Nike");
        requestDto.setSortType("price");
        requestDto.setSortOrder(SortOrder.ASC);

        byte[] content = objectMapper.writeValueAsBytes(requestDto);

        final String contentAsString = mockMvc.perform(post(API_PATH).contentType(MediaType.APPLICATION_JSON_VALUE).
                        content(content))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn().getResponse()
                .getContentAsString();
        List<ProductDto> actualProductsList = objectMapper.readValue(contentAsString, new TypeReference<>() {});
        List<ProductDto> expectedProductsList = new ArrayList<>();
        expectedProductsList.add(savedProductDto1);
        expectedProductsList.add(savedProductDto2);
        assertEquals(expectedProductsList, actualProductsList);
    }
}
