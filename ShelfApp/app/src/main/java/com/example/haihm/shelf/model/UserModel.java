package com.example.haihm.shelf.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Trần_Tân on 10/01/2018.
 */

public class UserModel implements Serializable {
    public String id;
    public String anhAvatar;
    public String anhCover;
    public String hoten;
    public String password;
    public String confirmPassword;
    public String sdt;
    public String diaC;
    public Rate rate;

    public UserModel() {
        id = "";
        anhAvatar ="";
        anhCover ="";
        hoten = "";
        sdt = "";
        diaC = "";
        rate = new Rate();
    }

    public UserModel(String id, String anhAvatar, String anhCover, String hoten, String sdt, String diaC, Rate rate) {
        this.id = id;
        this.anhAvatar = anhAvatar;
        this.anhCover = anhCover;
        this.hoten = hoten;
        this.sdt = sdt;
        this.diaC = diaC;
        this.rate = rate;
    }

    public UserModel(String id, String anhAvatar, String anhCover, String hoten, String password, String confirmPassword, String sdt, String diaC, Rate rate) {
        this.id = id;
        this.anhAvatar = anhAvatar;
        this.anhCover = anhCover;
        this.hoten = hoten;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.sdt = sdt;
        this.diaC = diaC;
        this.rate = rate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public class Rate {
        public int tongD;
        public int tongVote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnhAvatar() {
        return anhAvatar;
    }

    public void setAnhAvatar(String anhAvatar) {
        this.anhAvatar = anhAvatar;
    }

    public String getAnhCover() {
        return anhCover;
    }

    public void setAnhCover(String anhCover) {
        this.anhCover = anhCover;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaC() {
        return diaC;
    }

    public void setDiaC(String diaC) {
        this.diaC = diaC;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }
}
