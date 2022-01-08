package com.epam.customerservice.service;

import com.epam.customerservice.dto.ImageQueueResponseDto;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.ProductQueueResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public RabbitMqListener(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.productDto = new ProductDto();
    }

    @RabbitListener(queues = "products")
    public void productsListener(String message) throws JsonProcessingException, ParseException {
        ProductQueueResponseDto productQueueResponseDto = objectMapper.readValue(
                message, ProductQueueResponseDto.class);
        ProductDto productDto = productQueueResponseDto.getProductDto();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        productDto.setReleaseDateTime(format.parse(productQueueResponseDto.getCreationDate()));
        logger.info(
                "Got message from product queue: message type = {}, product entity = {}",
                productQueueResponseDto.getType(), productQueueResponseDto.getProductDto().toString()
        );
        if (productQueueResponseDto.getType().equals("save")) {
            productService.save(productDto);
        } else if (productQueueResponseDto.getType().equals("update")) {
            productService.update(productDto);
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
