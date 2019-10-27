package cz.muni.fi.pv168.cosplayrental;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OrderTableModel extends AbstractTableModel {

    private enum Column {

        PRODUCTNAME("Product name", String.class, OrderTableEntry::getName),
        PRICE("Price", Double.class, OrderTableEntry::getPrice),
        ISADDEDTOCART("Add to cart", Boolean.class, OrderTableEntry::isAddedToCart);

        private <T> Column(String name, Class<T> columnClass, Function<OrderTableEntry, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }

        private final String name;
        private final Class<?> columnClass;
        private final Function<OrderTableEntry, ?> extractor;
    }

    private List<OrderTableEntry> entries;

    public OrderTableModel(List<CatalogueEntry> entries) {
        this.entries = new ArrayList<>();
        for (CatalogueEntry ce : entries) {
            this.entries.add(new OrderTableEntry(ce));
        }
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
        OrderTableEntry entry = entries.get(rowIndex);
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
