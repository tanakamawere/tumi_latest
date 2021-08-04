package org.tmz.tumi.Objects;

public class BusinessObject {
    String Address, EmailAddress, Name, PhoneNumber, Type, Category, profilePicture, Key, Description;

    public BusinessObject() {
    }

    public BusinessObject(String address, String emailAddress, String name, String phoneNumber, String type, String category, String profilePicture, String key, String description) {
        Address = address;
        EmailAddress = emailAddress;
        Name = name;
        PhoneNumber = phoneNumber;
        Type = type;
        Category = category;
        this.profilePicture = profilePicture;
        Key = key;
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
