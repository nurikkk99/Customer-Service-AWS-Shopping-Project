package com.epam.customerservice.repository;

import com.epam.customerservice.entity.ImageEntity;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    List<ImageEntity> findAllByGoodId(long goodId);
}
