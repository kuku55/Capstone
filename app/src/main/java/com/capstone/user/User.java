package com.capstone.user;

public class User {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String gender;
    private String dob;
    private String mobileNumber;
    private String email;
    private String image;

    public User(){

    }

    public User(String userId, String userFirstName, String userLastName, String gender, String dob, String mobileNumber, String email, String image) {
        this.setUserId(userId);
        this.setUserFirstName(userFirstName);
        this.setUserLastName(userLastName);
        this.setGender(gender);
        this.setDob(dob);
        this.setMobileNumber(mobileNumber);
        this.setEmail(email);
        this.setImage(image);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getImage() { return image;}

    public void setImage(String image) { this.image = image; }
}

