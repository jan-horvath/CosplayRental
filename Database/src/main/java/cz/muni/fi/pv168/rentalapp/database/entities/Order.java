package cz.muni.fi.pv168.rentalapp.database.entities;

import java.time.LocalDate;
import java.util.List;

public class Order {

    private long id;
    private final List<ProductStack> productStacks  ;
    private final String email;
    private final String fullName;
    private final String phoneNumber;
    private LocalDate returnDate;

    public Order(List<ProductStack> productStacks, String email, String fullName, String phoneNumber, LocalDate returnDate) {
        this.productStacks = productStacks;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.returnDate = returnDate;
    }
    public Order(long id, List<ProductStack> productStacks, String email, String fullName, String phoneNumber, LocalDate returnDate) {
        this(productStacks, email, fullName, phoneNumber, returnDate);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long nid) {
        this.id = nid;
    }
    public List<ProductStack> getProductStacks() {
        return productStacks;
    }

    public String getEmail() {
        return email;
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
