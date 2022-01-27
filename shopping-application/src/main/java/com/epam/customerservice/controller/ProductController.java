package com.epam.customerservice.controller;

import com.epam.customerservice.dto.GetImageDto;
import com.epam.customerservice.dto.ProductDto;
import com.epam.customerservice.dto.SearchAndFilterRequestDto;
import com.epam.customerservice.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductDto findOne(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping()
    public List<ProductDto> searchAndFilter(@RequestBody SearchAndFilterRequestDto requestDto) {
        return productService.searchAndFilter(requestDto);
    }

    @GetMapping("/{id}/image")
    public List<GetImageDto> getAllImages(@PathVariable("id") long goodId) {
        return productService.getImagesByGoodId(goodId);
    }

    @GetMapping("/{id}/image/{imageId}")
    public GetImageDto getImage(@PathVariable("id") long goodId, @PathVariable("imageId") long imageId)  {
        return productService.getImageByImageId(imageId);
    }
}
