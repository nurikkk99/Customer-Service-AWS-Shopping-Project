package com.epam.customerservice.dto;

import com.epam.customerservice.dto.ProductDto;

public class ProductQueueResponseDto {
    private String type;
    private ProductDto productDto;
    private String creationDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProductDto getProductDto() {
        return productDto;
    }

    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

}
