package org.tmz.tumi.Objects;

public class MessageMainObject {
    String name, key, profilePicture;

    public MessageMainObject(String name, String key, String profilePicture) {
        this.name = name;
        this.key = key;
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
