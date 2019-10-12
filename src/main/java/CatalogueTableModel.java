import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CatalogueTableModel extends AbstractTableModel {

    private final List<CatalogueEntry> entries;

    public CatalogueTableModel(List<CatalogueEntry> entries) {
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
        CatalogueEntry entry = entries.get(rowIndex);

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
