import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class OrderTableModel extends AbstractTableModel {

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
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderTableEntry entry = entries.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return entry.getName();
            case 1:
                return entry.getPrice();
            case 2:
                return entry.isMarked();
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
            case 2:
                return "Add to cart";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column > 2) {
            throw new IndexOutOfBoundsException();
        }
        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Double.class;
            case 2:
                return Boolean.class;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            entries.get(rowIndex).flipMarked();
        }
    }
}
