package com.wavy.entity;

import java.sql.Timestamp;

/**
 * Created by WavyPeng on 2018/5/10.
 */
public class User {
    private long id;
    private String nickname;
    private String password;
    private String salt;
    private Timestamp register_date;
    private Timestamp last_login_date;
    private int login_count;

    public User(long id, String nickname, String password, String salt, Timestamp register_date,
                Timestamp last_login_date, int login_count) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.salt = salt;
        this.register_date = register_date;
        this.last_login_date = last_login_date;
        this.login_count = login_count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Timestamp getRegister_date() {
        return register_date;
    }

    public void setRegister_date(Timestamp register_date) {
        this.register_date = register_date;
    }

    public Timestamp getLast_login_date() {
        return last_login_date;
    }

    public void setLast_login_date(Timestamp last_login_date) {
        this.last_login_date = last_login_date;
    }

    public int getLogin_count() {
        return login_count;
    }

    public void setLogin_count(int login_count) {
        this.login_count = login_count;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", register_date=" + register_date +
                ", last_login_date=" + last_login_date +
                ", login_count=" + login_count +
                '}';
    }
}