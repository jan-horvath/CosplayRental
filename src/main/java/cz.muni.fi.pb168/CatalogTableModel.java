import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CatalogTableModel extends AbstractTableModel {

    private final List<CatalogEntry> entries;

    public CatalogTableModel(List<CatalogEntry> entries) {
        this.entries = entries;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CatalogEntry entry = entries.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return entry.getName();
            case 1:
                return entry.getPrice();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Item name";
            case 1:
                return "Price";
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
