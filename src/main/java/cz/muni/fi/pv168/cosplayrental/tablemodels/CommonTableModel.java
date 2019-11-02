package cz.muni.fi.pv168.cosplayrental.tablemodels;

import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Function;

public class CommonTableModel extends AbstractTableModel  {

    public List<ProductStack> entries;

    public CommonTableModel(List<ProductStack> entries) { this.entries = entries; }

    public enum Column {

        PRODUCTNAME("Product name", String.class, ProductStack::getName),
        PRODUCTSIZE("Product size", Enum.class, ProductStack::getSize),
        PRICE("Price", Double.class, ProductStack::getPrice),
        AVAILABLEITEMS("Available items", Integer.class, ProductStack::getStackSize),
        ADDTOCART("Add to cart", Boolean.class, ProductStack::isAddedToCart);

        private final String name;
        private final Class<?> columnClass;
        private final Function<ProductStack, ?> extractor;

        private <T> Column(String name, Class<T> columnClass, Function<ProductStack, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }
    }

    @Override
    public int getRowCount() { return entries.size(); }

    @Override
    public int getColumnCount() { return Column.values().length; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProductStack entry = entries.get(rowIndex);
        return Column.values()[columnIndex].extractor.apply(entry);
    }

    @Override
    public String getColumnName(int column) {
        return Column.values()[column].name;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return Column.values()[column] == Column.ADDTOCART;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.values()[columnIndex].columnClass;
    }

    @Override
    //TODO Can I remove "if" and do something like entries.get(rowIndex).setAddedToCart( (getColumnClass(columnIndex)) aValue); (generic casting)
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (Column.values()[columnIndex] == Column.AVAILABLEITEMS) {
            entries.get(rowIndex).setStackSize((int) aValue);
        }
    }
}
