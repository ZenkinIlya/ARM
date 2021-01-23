package com.qs5501.demo.settings.entity;

import java.io.Serializable;

public class Operator implements Serializable {

    private String post;
    private String name;
    private String secondName;
    private String thirdName;
    private String login;
    private String password;
    private long inn;

    public Operator(){};

    public Operator(String post, String name, String secondName, String thirdName, String login, String password, long inn) {
        this.post = post;
        this.name = name;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.login = login;
        this.password = password;
        this.inn = inn;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "post='" + post + '\'' +
                ", name='" + name + '\'' +
                ", secondName='" + secondName + '\'' +
                ", thirdName='" + thirdName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", inn=" + inn +
                '}';
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getInn() {
        return inn;
    }

    public void setInn(long inn) {
        this.inn = inn;
    }
}
