package com.prefer_music_store.app.repo;

public class UserVO {
    String username, password, name, birth, email, recentRatingDateTime, ratingCount;
    int gender, age;

    public UserVO () {}

    public UserVO(String username, String name, String birth, String email, int gender, int age) {
        this.username = username;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }

    public UserVO(String username, String password, String name, String birth, String email, int gender, int age) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }

    public UserVO(String username, String password, String name, String birth, String email, String recentRatingDateTime, String ratingCount, int gender, int age) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.recentRatingDateTime = recentRatingDateTime;
        this.ratingCount = ratingCount;
        this.gender = gender;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getBirth() {
        return birth;
    }

    public String getEmail() {
        return email;
    }

    public String getRecentRatingDateTime() {
        return recentRatingDateTime;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRecentRatingDateTime(String recentRatingDateTime) {
        this.recentRatingDateTime = recentRatingDateTime;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", email='" + email + '\'' +
                ", recentRatingDateTime='" + recentRatingDateTime + '\'' +
                ", ratingCount='" + ratingCount + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                '}';
    }
}
