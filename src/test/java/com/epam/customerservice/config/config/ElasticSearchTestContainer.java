package com.epam.customerservice.config.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class ElasticSearchTestContainer {

    DockerImageName elasticSearchImage = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.6.2");

    @Bean (initMethod = "start", destroyMethod = "stop")
    ElasticsearchContainer elasticsearchContainer () {
        ElasticsearchContainer elasticsearchContainer =  new ElasticsearchContainer(elasticSearchImage);
        return elasticsearchContainer;
    }
}
