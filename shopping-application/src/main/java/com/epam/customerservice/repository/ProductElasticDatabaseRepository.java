package com.epam.customerservice.repository;

import com.epam.customerservice.entity.ProductEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ProductElasticDatabaseRepository extends ElasticsearchRepository<ProductEntity, Long> {
}
