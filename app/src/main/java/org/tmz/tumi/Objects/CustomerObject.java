package org.tmz.tumi.Objects;

public class CustomerObject {
    public String name, phone, customerKey;

    public CustomerObject(
            String name,
            String phone,
            String customerKey) {
        this.name = name;
        this.phone = phone;
        this.customerKey = customerKey;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getCustomerKey() {
        return customerKey;
    }
}
