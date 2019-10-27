package cz.muni.fi.pv168.cosplayrental.tableentries;

public class CatalogueEntry {
    private final String name;
    private final double price;
    private boolean isAddedToCart;


    public CatalogueEntry(String name, double price) {
        this.name = name;
        this.price = price;
        this.isAddedToCart = false;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setAddedToCart(boolean addedToCart) {
        this.isAddedToCart = addedToCart;
    }

    public boolean isAddedToCart() {
        return isAddedToCart;
    }
}
