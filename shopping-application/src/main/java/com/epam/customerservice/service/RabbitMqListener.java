package com.epam.customerservice.service;

import com.epam.customerservice.dto.ImageQueueResponseDto;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.ProductIdQueueResponseDto;
import com.epam.customerservice.feign.AdminFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@EnableRabbit
public class RabbitMqListener {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqListener.class);

    private ProductService productService;
    private ObjectMapper objectMapper;
    private ProductDto productDto;
    private AdminFeignClient adminFeignClient;

    public RabbitMqListener(ProductService productService, ObjectMapper objectMapper, AdminFeignClient adminFeignClient) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.productDto = new ProductDto();
        this.adminFeignClient = adminFeignClient;
    }

    @RabbitListener(queues = "products")
    public void productsListener(String message) throws JsonProcessingException {
        ProductIdQueueResponseDto productIdQueueResponseDto = objectMapper.readValue(
                message, ProductIdQueueResponseDto.class);
        Long productId = productIdQueueResponseDto.getProductId();
        logger.info(
                "Got message from product queue: message type = {}, product id = {}",
                productIdQueueResponseDto.getType(), productId
        );
        if (productIdQueueResponseDto.getType().equals("save")) {
            ProductDto productDto = adminFeignClient.findOne(productId);
            productService.save(productDto);
            logger.info("Product was saved: {}", productDto);
        }
        else if (productIdQueueResponseDto.getType().equals("update")) {
            ProductDto productDto = adminFeignClient.findOne(productId);
            productService.update(productDto);
            logger.info("Product was updated - {}", productDto);
        }
    }


    @RabbitListener(queues = "images")
    public void imagesListener(String message) throws JsonProcessingException {
        ImageQueueResponseDto imageQueueResponseDto = objectMapper.readValue(message, ImageQueueResponseDto.class);
        logger.info(
                "Got message from images queue: message type = {}, product entity = {}",
                imageQueueResponseDto.getType(), imageQueueResponseDto.getImageDto().toString()
        );
        if (imageQueueResponseDto.getType().equals("save")) {
            productService.saveImage(imageQueueResponseDto.getImageDto());
        } else if (imageQueueResponseDto.getType().equals("delete")) {
            productService.deleteImage(imageQueueResponseDto.getImageDto());
        }
    }
}
