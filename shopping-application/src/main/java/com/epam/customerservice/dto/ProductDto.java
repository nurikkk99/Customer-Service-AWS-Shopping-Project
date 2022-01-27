package com.epam.customerservice.dto;

import com.epam.customerservice.entity.ProductEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto implements EntityDtoMapper<ProductDto, ProductEntity>{
    private Long id;
    private String name;
    private GoodsType type;
    private BigDecimal price;
    private String manufacturer;
    private Date releaseDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoodsType getType() {
        return type;
    }

    public void setType(GoodsType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(Date releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }


    @Override
    public ProductDto entityToDto(ProductEntity entity) {
        ProductDto productDto = new ProductDto();
        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        Optional.ofNullable(entity.getType()).ifPresent(x -> productDto.setType(GoodsType.valueOf(x)));
        productDto.setPrice(entity.getPrice());
        productDto.setManufacturer(entity.getManufacturer());
        productDto.setReleaseDateTime(entity.getReleaseDateTime());
        return productDto;
    }

    @Override
    public ProductEntity dtoToEntity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(this.id);
        productEntity.setName(this.name);
        Optional.ofNullable(this.type).ifPresent(x -> productEntity.setType(x.toString()));
        productEntity.setPrice(this.price);
        productEntity.setManufacturer(this.getManufacturer());
        productEntity.setReleaseDateTime(this.releaseDateTime);
        return productEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductDto{" + "id=" + id + ", name='" + name + '\'' + ", type=" + type + ", price=" + price
                + ", manufacturer='" + manufacturer + '\'' + ", releaseDateTime=" + releaseDateTime + '}';
    }
}
