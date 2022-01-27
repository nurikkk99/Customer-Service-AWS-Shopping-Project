package com.epam.customerservice.service;

import com.epam.customerservice.dto.GetImageDto;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.SearchAndFilterRequestDto;
import com.epam.customerservice.entity.ImageEntity;
import com.epam.customerservice.entity.ProductEntity;
import com.epam.customerservice.exception.EntityNotFoundException;
import com.epam.customerservice.helper.Indices;
import com.epam.customerservice.repository.ProductElasticDatabaseRepository;
import com.epam.customerservice.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    private RestHighLevelClient client;
    private ProductElasticDatabaseRepository productElasticDatabaseRepository;
    private ImageRepository imageRepository;
    private ProductDto productDto;
    private GetImageDto getImageDto;

    public ProductService(
            RestHighLevelClient client, ProductElasticDatabaseRepository productElasticDatabaseRepository,
            ImageRepository imageRepository
    ) {
        this.client = client;
        this.productElasticDatabaseRepository = productElasticDatabaseRepository;
        productDto = new ProductDto();
        getImageDto = new GetImageDto();
        this.imageRepository = imageRepository;
    }

    public ProductDto findById(final Long id) {
        ProductEntity productEntity = productElasticDatabaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product entity with id " + id + " does not exist"));
        return productDto.entityToDto(productEntity);
    }

    public ProductDto save(final ProductDto productEntity) {
        logger.info("Product entity with id {} is saving in elastic database", productEntity.getId());
        ProductDto savedProductDto = productDto.entityToDto(
                productElasticDatabaseRepository.save(productEntity.dtoToEntity()));
        logger.info("Product entity with id {} was saved in elastic database", savedProductDto.getId());
        return savedProductDto;
    }

    public void deleteAll() {
        logger.info("Deleting all products in elastic database");
        productElasticDatabaseRepository.deleteAll();
    }

    public List<ProductDto> searchAndFilter(SearchAndFilterRequestDto requestDto) {
        SearchRequest request = SearchAndFilterRequestBuilder.buildSearchAndFilterRequest(
                Indices.PRODUCT_INDEX, requestDto);

        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
            List<ProductDto> products = new ArrayList<>(searchHits.length);

            for (SearchHit hit : searchHits) {
                products.add(objectMapper.readValue(hit.getSourceAsString(), ProductDto.class));
            }
            return products;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void update(ProductDto productDto) {
        ProductEntity productEntity = productElasticDatabaseRepository.findById(productDto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Product entity with id " + productDto.getId() + " does not exist in elastic database"));
        logger.info("Product entity with id {} is updating in elastic database", productDto.getId());
        productElasticDatabaseRepository.save(productDto.dtoToEntity());
    }

    public void saveImage(GetImageDto imageDto) {
        logger.info("Saving image of product with image id {} in relational database", imageDto.getImageId());
        imageRepository.save(imageDto.dtoToEntity());
    }

    public void deleteImage(GetImageDto imageDto) {
        logger.info("Deleting image with image id {} from relational database", imageDto.getImageId());
        imageRepository.deleteById(imageDto.getImageId());
    }

    public List<GetImageDto> getImagesByGoodId(long goodId) {
        List<ImageEntity> imageList = imageRepository.findAllByGoodId(goodId);
        if (imageList.isEmpty()) {
            throw new EntityNotFoundException("Good entity with id " + goodId + "does not exist");
        }
        return imageList.stream().map(x -> getImageDto.entityToDto(x)).collect(Collectors.toList());
    }

    public GetImageDto getImageByImageId(long imageId) {
        ImageEntity imageEntity = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image with id " + imageId + " does not exist"));
        return getImageDto.entityToDto(imageEntity);
    }

    public void deleteAllImages() {
        logger.info("Deleting all images in relational database");
        imageRepository.deleteAll();
    }
}
