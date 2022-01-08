package com.epam.customerservice.repository;

import com.epam.customerservice.entity.RegisteredUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUserEntity, Long> {

}
