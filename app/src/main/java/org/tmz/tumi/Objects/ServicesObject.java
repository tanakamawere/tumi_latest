package org.tmz.tumi.Objects;

public class ServicesObject {
    String Name, Type, profilePicture, Key, Description, Rating;

    public ServicesObject(String name, String type, String profilePicture, String key, String description, String rating) {
        Name = name;
        Type = type;
        this.profilePicture = profilePicture;
        Key = key;
        Description = description;
        Rating = rating;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getKey() {
        return Key;
    }

    public String getDescription() {
        return Description;
    }

    public String getRating() {
        return Rating;
    }
}
