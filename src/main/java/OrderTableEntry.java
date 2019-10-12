public class OrderTableEntry {
    private final String name;
    private final double price;
    private boolean marked;


    public OrderTableEntry(CatalogueEntry catalogueEntry) {
        this.name = catalogueEntry.getName();
        this.price = catalogueEntry.getPrice();
        this.marked = false;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void flipMarked() {
        this.marked = !this.marked;
    }

    public boolean isMarked() {
        return marked;
    }
}
