package com.epam.customerservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private long sessionID;

    @ManyToOne
    @JoinColumn(name = "registered_user_id")
    public RegisteredUserEntity registeredUser;

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public RegisteredUserEntity getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(RegisteredUserEntity registeredUser) {
        this.registeredUser = registeredUser;
    }
}
