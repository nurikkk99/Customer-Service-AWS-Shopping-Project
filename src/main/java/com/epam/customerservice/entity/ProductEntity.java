package com.epam.customerservice.entity;

import com.epam.customerservice.helper.Indices;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = Indices.PRODUCT_INDEX)
@Setting(settingPath = "static/es-setting.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEntity {

    @Id
    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String type;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Text)
    private String manufacturer;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
}
