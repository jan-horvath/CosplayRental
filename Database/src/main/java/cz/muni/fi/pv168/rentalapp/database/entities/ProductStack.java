package cz.muni.fi.pv168.rentalapp.database.entities;

import java.util.Objects;

public class ProductStack {

    public enum Size {
        NA,XS,S,M,L,XL,XXL
    }

    // id attributes added because of database incorporation. Just WIP version.
    // question: final key word
    private long id;
    private long storeId;
    private final String name;
    private final Size size;
    private double price;
    private int stackSize;

    public ProductStack(String name, Size size, double price, int stackSize) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.stackSize = stackSize;
    }

    public ProductStack(ProductStack other) {
        name = other.name;
        size = other.size;
        price = other.price;
        stackSize = other.stackSize;
    }

    public ProductStack(long id, long storeId, String name, Size size, double price, int stackSize) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.stackSize = stackSize;
        this.id = id;
        this.storeId = storeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long nid) { this.id = nid; };

    public String getName() {
        return name;
    }

    public long getStoreId() {
        return storeId;
    }

    public Size getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductStack that = (ProductStack) o;
        return name.equals(that.name) &&
                size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }

    @Override
    public String toString() {
        return  name + " (" + size + "):  " + stackSize + " pc" + ((stackSize > 1) ? "s" : "");
    }
}
