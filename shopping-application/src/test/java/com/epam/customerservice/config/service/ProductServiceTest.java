package com.epam.customerservice.config.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.customerservice.config.config.TestContainerConfig;
import com.epam.customerservice.dto.GoodsType;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.SearchAndFilterRequestDto;
import com.epam.customerservice.service.ProductService;
import com.epam.customerservice.service.RabbitMqListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestContainerConfig.class)
@Testcontainers
@ActiveProfiles("test")
public class ProductServiceTest {

    private ProductDto savedProductDto1;

    @MockBean
    RabbitMqListener rabbitMqListener;

    @Autowired
    private TestContainerConfig testContainerConfig;

    @Autowired
    private ProductService productService;

    @BeforeEach
    public void prepareData() throws ParseException {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Air Force");
        productDto.setManufacturer("Nike");
        productDto.setPrice(BigDecimal.valueOf(7000));
        productDto.setType(GoodsType.Sneakers);
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String string1 = "2021-12-27T13:00:00.000-0700";
        productDto.setReleaseDateTime(df1.parse(string1));
        savedProductDto1 = productService.save(productDto);

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setName("Air");
        productDto2.setManufacturer("Adidas");
        productDto2.setPrice(BigDecimal.valueOf(3000));
        productDto2.setType(GoodsType.Sneakers);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String string2 = "2021-12-30T13:00:00.000-0700";
        productDto2.setReleaseDateTime(df2.parse(string2));
        productService.save(productDto2);
    }

    @AfterEach
    public void dropData() {
        productService.deleteAll();
    }

    @Test
    public void findByIdTest() {
        ProductDto expectedProduct = savedProductDto1;
        ProductDto actualProduct = productService.findById(savedProductDto1.getId());
        assertEquals(expectedProduct.getId(), actualProduct.getId());
    }

    @Test
    public void searchAndFilterShouldSearchByNameTest() {
        SearchAndFilterRequestDto requestDto= new SearchAndFilterRequestDto();
        List<String> searchFields = new ArrayList<>();
        searchFields.add("name");
        requestDto.setSearchFields(searchFields);
        requestDto.setSearchTerm("Air");
        List<ProductDto> searchedProducts = productService.searchAndFilter(requestDto);
        assertTrue(searchedProducts.size()==2);
        assertTrue(searchedProducts.stream().allMatch(x->x.getName().contains("Air")));
    }

    @Test
    public void searchAndFilterShouldFilterByTypeAndManufacturerTest() {
        SearchAndFilterRequestDto requestDto= new SearchAndFilterRequestDto();
        requestDto.setType("Sneakers");
        requestDto.setManufacturer("Nike");
        List<ProductDto> searchedProducts = productService.searchAndFilter(requestDto);
        assertTrue(searchedProducts.size()==1);
        assertTrue(searchedProducts.stream()
                .allMatch(x -> x.getManufacturer().equals("Nike") && x.getType().name().equals("Sneakers")));
    }

    @Test
    public void searchAndFilterShouldSortByPriceTest() {
        SearchAndFilterRequestDto requestDto= new SearchAndFilterRequestDto();
        requestDto.setSortType("price");
        requestDto.setSortOrder(SortOrder.ASC);
        List<ProductDto> searchedProducts = productService.searchAndFilter(requestDto);
        assertTrue(searchedProducts.size()==2);
        assertTrue(searchedProducts.get(0).getPrice().compareTo(searchedProducts.get(1).getPrice()) < 0);

        SearchAndFilterRequestDto requestDto2= new SearchAndFilterRequestDto();
        requestDto2.setSortType("price");
        requestDto2.setSortOrder(SortOrder.DESC);
        List<ProductDto> searchedProducts2 = productService.searchAndFilter(requestDto2);
        assertTrue(searchedProducts2.size()==2);
        assertTrue(searchedProducts2.get(0).getPrice().compareTo(searchedProducts2.get(1).getPrice()) > 0);
    }

    @Test
    public void searchAndFilterShouldSortByDateTest() {
        SearchAndFilterRequestDto requestDto= new SearchAndFilterRequestDto();
        requestDto.setSortType("releaseDateTime");
        requestDto.setSortOrder(SortOrder.ASC);
        List<ProductDto> searchedProducts = productService.searchAndFilter(requestDto);
        assertTrue(searchedProducts.size()==2);
        assertTrue(searchedProducts.get(0).getReleaseDateTime().before(searchedProducts.get(1).getReleaseDateTime()));

        SearchAndFilterRequestDto requestDto2= new SearchAndFilterRequestDto();
        requestDto2.setSortType("releaseDateTime");
        requestDto2.setSortOrder(SortOrder.DESC);
        List<ProductDto> searchedProducts2 = productService.searchAndFilter(requestDto2);
        assertTrue(searchedProducts2.size()==2);
        assertTrue(searchedProducts2.get(0).getReleaseDateTime().after(searchedProducts2.get(1).getReleaseDateTime()));
    }
}
