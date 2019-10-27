package cz.muni.fi.pv168.cosplayrental.tablemodels;

import cz.muni.fi.pv168.cosplayrental.tableentries.CatalogueEntry;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Function;

//TODO Is it possible to merge Catalogue and Order table models? The only difference is the modifiable add-to-cart option
public class AddToCartTableModel extends AbstractTableModel {

    private enum Column {

        PRODUCTNAME("Product name", String.class, CatalogueEntry::getName),
        PRICE("Price", Double.class, CatalogueEntry::getPrice),
        ISADDEDTOCART("Add to cart", Boolean.class, CatalogueEntry::isAddedToCart);

        private <T> Column(String name, Class<T> columnClass, Function<CatalogueEntry, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }

        private final String name;
        private final Class<?> columnClass;
        private final Function<CatalogueEntry, ?> extractor;
    }

    private List<CatalogueEntry> entries;

    public AddToCartTableModel(List<CatalogueEntry> entries) {
        this.entries = entries;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CatalogueEntry entry = entries.get(rowIndex);
        return Column.values()[columnIndex].extractor.apply(entry);
    }

    @Override
    public String getColumnName(int column) {
        return Column.values()[column].name;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return Column.values()[column] == Column.ISADDEDTOCART;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.values()[columnIndex].columnClass;
    }

    @Override
    //TODO Can I remove "if" and do something like entries.get(rowIndex).setAddedToCart( (getColumnClass(columnIndex)) aValue); (generic casting)
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (Column.values()[columnIndex] == Column.ISADDEDTOCART) {
            entries.get(rowIndex).setAddedToCart((Boolean) aValue);
        }
    }
}
