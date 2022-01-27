package com.epam.customerservice.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "registered_user")
public class RegisteredUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "address")
    private String address;

    @Column(name = "prone_number")
    private long phoneNUmber;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "registeredUser")
    private List<UserEntity> usersList;

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

    public Long getPhoneNUmber() {
        return phoneNUmber;
    }

    public void setPhoneNUmber(Long phoneNUmber) {
        this.phoneNUmber = phoneNUmber;
    }

    public List<UserEntity> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserEntity> usersList) {
        this.usersList = usersList;
    }

}
