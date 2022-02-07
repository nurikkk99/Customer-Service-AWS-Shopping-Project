package com.epam.customerservice.feign;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.epam.customerservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "admin-service", url = "${admin-service.url}", path = "api/goods")
public interface AdminFeignClient {

    @GetMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    ProductDto findOne(@PathVariable("id") Long productId);
}
