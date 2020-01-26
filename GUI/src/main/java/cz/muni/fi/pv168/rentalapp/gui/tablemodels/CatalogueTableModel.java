package cz.muni.fi.pv168.rentalapp.gui.tablemodels;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.Collections;

public class CatalogueTableModel extends AbstractTableModel  {

    public List<ProductStack> entries;
    private List<Integer> piecesSelected;
    private DataManager dataManager;

    public CatalogueTableModel(DataManager dataManager) {
        this.dataManager = dataManager;
        reloadData();
        this.piecesSelected = new ArrayList<>(Collections.nCopies(entries.size(), 0));
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
            return piecesSelected.get(rowIndex);
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
            int availableStackSize = entries.get(rowIndex).getStackSize();
            if ((int) aValue > availableStackSize) {
                piecesSelected.set(rowIndex, availableStackSize);
            } else {
                if ((int) aValue < 0) {
                    piecesSelected.set(rowIndex, 0);
                } else {
                    piecesSelected.set(rowIndex, (Integer) aValue);
                }
            }

        } else {
            if (Column.values()[columnIndex] == Column.AVAILABLEITEMS) {
                entries.get(rowIndex).setStackSize((Integer) aValue);
            }
        }
    }

    public boolean areAllItemsZero() {
        boolean allZero = true;
        for (Integer i : piecesSelected) {
            if (i > 0) {
                allZero = false;
            }
        }
        return allZero;
    }

    public void setAllAddToCartItemsToZero() {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(0, i, getColumnCount()-1);
        }
        fireTableDataChanged();
    }

    public List<ProductStack> getProductStackWithSelectedPieces() {
        List<ProductStack> selectedStacks = new ArrayList<>();

        for (int i = 0; i < entries.size(); ++i) {
            ProductStack selectedProductStack = new ProductStack(entries.get(i));
            selectedProductStack.setStackSize(piecesSelected.get(i));
            selectedStacks.add(selectedProductStack);
        }

        return selectedStacks;
    }

    public void reloadData() {
        entries = dataManager.getAllCatalogueData();
    }
}
