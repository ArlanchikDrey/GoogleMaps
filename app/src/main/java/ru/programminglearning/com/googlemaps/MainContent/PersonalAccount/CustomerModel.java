package ru.programminglearning.com.googlemaps.MainContent.PersonalAccount;

public class CustomerModel {

    private String userFromCity;
    private String userName;
    private String userPhoneNumber;
    private String userEmail;

    public CustomerModel(String userName,
                         String userFromCity,
                         String userPhoneNumber,
                         String userEmail) {
        this.userFromCity = userFromCity;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
    }

    public String getUserFromCity() {
        return userFromCity;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
