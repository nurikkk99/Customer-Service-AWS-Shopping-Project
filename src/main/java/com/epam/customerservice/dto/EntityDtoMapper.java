package com.epam.customerservice.dto;

public interface EntityDtoMapper<T,E> {

  T entityToDto(E entity);

  E dtoToEntity();
}
