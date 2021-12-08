package com.servecreative.omeet.models;

public class User {
    String uid,name,profile;
     long coins;
    public User(){}

    public User(String uid, String name, String profile,long coins) {
        this.uid = uid;
        this.name = name;
        this.profile = profile;
        this.coins=coins;

    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


}
