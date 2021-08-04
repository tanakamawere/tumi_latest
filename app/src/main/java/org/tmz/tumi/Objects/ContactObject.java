package org.tmz.tumi.Objects;

public class ContactObject {
    public String name, phone;

    public ContactObject(
            String name,
            String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
