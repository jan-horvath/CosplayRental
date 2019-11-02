package cz.muni.fi.pv168.cosplayrental.entities;

import java.util.List;

public class Order {
    private final List<ProductStack> productStacks;
    private final String email;
    private final String creditCardNumber;
    private final String fullName;
    private final String phoneNumber;

    public Order(List<ProductStack> productStacks, String email, String creditCardNumber, String fullName, String phoneNumber) {
        this.productStacks = productStacks;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public List<ProductStack> getProductStacks() {
        return productStacks;
    }

    public String getEmail() {
        return email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
