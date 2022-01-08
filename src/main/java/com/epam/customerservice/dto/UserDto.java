package com.epam.customerservice.dto;

import com.epam.customerservice.entity.RegisteredUserEntity;
import java.util.Objects;
import javax.validation.constraints.Null;

public class UserDto implements EntityDtoMapper<UserDto, RegisteredUserEntity>{

    @Null(message = "Entity id shouldn't be specified explicitly in request body")
    private Long id;
    private String name;
    private String surname;
    private String address;
    private Long phoneNumber;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public UserDto entityToDto(RegisteredUserEntity entity) {
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setName(entity.getName());
        userDto.setSurname(entity.getSurname());
        userDto.setAddress(entity.getAddress());
        userDto.setPhoneNumber(entity.getPhoneNUmber());
        return userDto;
    }

    @Override
    public RegisteredUserEntity dtoToEntity() {
        RegisteredUserEntity registeredUser = new RegisteredUserEntity();
        registeredUser.setId(this.id);
        registeredUser.setName(this.name);
        registeredUser.setSurname(this.surname);
        registeredUser.setAddress(this.address);
        registeredUser.setPhoneNUmber(this.phoneNumber);
        return registeredUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserDto{" + "id=" + id + ", name='" + name + '\'' + ", surname='" + surname + '\'' + ", address='"
                + address + '\'' + ", phoneNumber=" + phoneNumber + '}';
    }
}
