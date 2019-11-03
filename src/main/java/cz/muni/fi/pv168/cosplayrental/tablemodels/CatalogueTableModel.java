package cz.muni.fi.pv168.cosplayrental.tablemodels;

import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.Collections;

public class CatalogueTableModel extends AbstractTableModel  {

    public List<ProductStack> entries;
    private List<Integer> piecesOrdered;

    public CatalogueTableModel(List<ProductStack> entries) {
        this.entries = entries;
        this.piecesOrdered = new ArrayList<>(Collections.nCopies(entries.size(), 0));
    }

    public enum Column {

        PRODUCTNAME("Product name", String.class, ProductStack::getName),
        PRODUCTSIZE("Product size", Enum.class, ProductStack::getSize),
        PRICE("Price", Double.class, ProductStack::getPrice),
        AVAILABLEITEMS("Available items", Integer.class, ProductStack::getStackSize);

        private final String name;
        private final Class<?> columnClass;
        private final Function<ProductStack, ?> extractor;

        <T> Column(String name, Class<T> columnClass, Function<ProductStack, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }
    }

    @Override
    public int getRowCount() { return entries.size(); }

    @Override
    public int getColumnCount() { return Column.values().length + 1; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == Column.values().length) {
            return piecesOrdered.get(rowIndex);
        }
        ProductStack entry = entries.get(rowIndex);
        return Column.values()[columnIndex].extractor.apply(entry);
    }

    @Override
    public String getColumnName(int column) {
        if (column == Column.values().length ) {
            return "Ordered items";
        }
        return Column.values()[column].name;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
         return column == Column.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == Column.values().length) {
            return Integer.class;
        }
        return Column.values()[columnIndex].columnClass;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == Column.values().length) {
            piecesOrdered.set(rowIndex, (Integer) aValue);
        }

        if ((Integer) aValue > entries.get(rowIndex).getStackSize()) {
            piecesOrdered.set(rowIndex, (Integer) 0);
        }
    }

    public ProductStack getOrderedProductStack(int row) {
        return entries.get(row);
    }

    public boolean areAllItemsZero() {
        boolean allZero = true;
        for (Integer i : piecesOrdered) {
            if (i > 0) {
                allZero = false;
            }
        }
        return allZero;
    }
}
