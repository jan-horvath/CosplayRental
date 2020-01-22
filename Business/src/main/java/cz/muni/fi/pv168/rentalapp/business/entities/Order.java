package cz.muni.fi.pv168.rentalapp.business.entities;

import java.time.LocalDate;
import java.util.List;

public class Order {

    private static int id_gen = 0;
    private static int generateId() {
        return id_gen++;
    }

    private final long id;
    private final List<ProductStack> productStacks  ;
    private final String email;
    private final String creditCardNumber;
    private final String fullName;
    private final String phoneNumber;
    private LocalDate returnDate;

    public Order(List<ProductStack> productStacks, String email, String creditCardNumber, String fullName, String phoneNumber, LocalDate returnDate) {
        this.id = generateId();
        this.productStacks = productStacks;
        this.fullName = fullName;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.phoneNumber = phoneNumber;
        this.returnDate = returnDate;
    }

    public long getId() {
        return id;
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
