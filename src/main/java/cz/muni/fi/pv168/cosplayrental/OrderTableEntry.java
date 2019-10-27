package cz.muni.fi.pv168.cosplayrental;

public class OrderTableEntry {
    private final String name;
    private final double price;
    private boolean isAddedToCart;


    public OrderTableEntry(CatalogueEntry catalogueEntry) {
        this.name = catalogueEntry.getName();
        this.price = catalogueEntry.getPrice();
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

    public void flipAddedToCart() {
        this.isAddedToCart = !this.isAddedToCart;
    }

    public boolean isAddedToCart() {
        return isAddedToCart;
    }
}
