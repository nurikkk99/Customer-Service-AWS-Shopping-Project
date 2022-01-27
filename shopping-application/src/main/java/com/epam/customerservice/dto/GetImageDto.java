package com.epam.customerservice.dto;

import com.epam.customerservice.entity.ImageEntity;
import java.util.Objects;

public class GetImageDto implements EntityDtoMapper<GetImageDto, ImageEntity>{
    private Long imageId;
    private Long goodId;
    private String imageURI;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    @Override
    public GetImageDto entityToDto(ImageEntity imageEntity) {
        GetImageDto imageDto = new GetImageDto();
        imageDto.setImageId(imageEntity.getId());
        imageDto.setImageURI(imageEntity.getImageURI());
        imageDto.setGoodId(imageEntity.getId());
        return imageDto;
    }

    @Override
    public ImageEntity dtoToEntity() {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(this.imageId);
        imageEntity.setImageURI(this.imageURI);
        imageEntity.setGoodId(this.goodId);
        return imageEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetImageDto that = (GetImageDto) o;
        return imageId == that.imageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId);
    }

    @Override
    public String toString() {
        return "GetImageDto{" + "imageId=" + imageId + ", goodId=" + goodId + ", imageURI='" + imageURI + '\'' + '}';
    }
}
