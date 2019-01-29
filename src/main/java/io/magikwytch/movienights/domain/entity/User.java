package io.magikwytch.movienights.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private String userID;
    private String name;
    private String userEmail;
    private String refreshToken;
    private String accessToken;
    private Long accessTokenExpiration;

    private User() {
    }

    public User(String userID, String name, String userEmail, String refreshToken, String accessToken, Long accessTokenExpiration) {
        this.userID = userID;
        this.name = name;
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenExpiration=" + accessTokenExpiration +
                '}';
    }
}
