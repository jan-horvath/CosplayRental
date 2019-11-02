package cz.muni.fi.pv168.cosplayrental.tablemodels;

import cz.muni.fi.pv168.cosplayrental.tableentries.CatalogueEntry;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Function;

public class CatalogueTableModel extends AbstractTableModel {

    private enum Column {

        PRODUCTNAME("Product name", String.class, CatalogueEntry::getName),
        PRICE("Price", Double.class, CatalogueEntry::getPrice);

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

    public CatalogueTableModel(List<CatalogueEntry> entries) {
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
}