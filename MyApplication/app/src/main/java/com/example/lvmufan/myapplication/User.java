package com.example.lvmufan.myapplication;

public class User {
    private String username;
    private String password;
    private String mail;
    private String sex;
    private String phoneNumber;
    private String token;

    public void User(){}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setToken(String Token) {
        this.token = Token;
    }
    public String getToken() {
        return token;
    }
}