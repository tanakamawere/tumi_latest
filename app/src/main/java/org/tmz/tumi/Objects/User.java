package org.tmz.tumi.Objects;

public class User {
    //These string names should be similar to the ones in the database
    private String fullName, phoneNumber, emailAddress, dateJoined, position, profilePicture;

    //This must be a constructor
    public User() {
    }

    public User(
            String userFullName,
            String userPhoneNumber,
            String userDateJoined,
            String userEmailAddress,
            String userPosition,
            String profilePicture) {
        this.fullName = userFullName;
        this.phoneNumber = userPhoneNumber;
        this.dateJoined = userDateJoined;
        this.emailAddress = userEmailAddress;
        this.position = userPosition;
        this.profilePicture = profilePicture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getPosition() {
        return position;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}

