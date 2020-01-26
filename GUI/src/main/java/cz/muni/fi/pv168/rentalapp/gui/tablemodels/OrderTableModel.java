package cz.muni.fi.pv168.rentalapp.gui.tablemodels;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class OrderTableModel extends AbstractTableModel {


    private enum Column {

        PRODUCT_STACKS("Products", List.class, Order::getProductStacks),
        EMAIL("Email", String.class, Order::getEmail),
        FULL_NAME("Name", String.class, Order::getFullName),
        PHONE_NUMBER("Phone", String.class, Order::getPhoneNumber),
        RETURN_DATE("Return date", LocalDate.class, Order::getReturnDate);

        <T> Column(String name, Class<T> columnClass, Function<Order, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }

        private final String name;
        private final Class<?> columnClass;
        private final Function<Order, ?> extractor;
    }

    private List<Order> entries;
    private DataManager dataManager;

    public OrderTableModel(DataManager dataManager) throws DatabaseException {
        this.dataManager = dataManager;
        reloadData();
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
        Order entry = entries.get(rowIndex);
        Column column = Column.values()[columnIndex];
        if (column == Column.RETURN_DATE) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return ((LocalDate) column.extractor.apply(entry)).format(dtf);
        }
        return column.extractor.apply(entry);
    }

    @Override
    public String getColumnName(int column) {
        return Column.values()[column].name;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.values()[columnIndex].columnClass;
    }

    public List<Order> getEntries() {
        return entries;
    }

    public void addOrderToEntries(Order order) {
        entries.add(order);
    }

    public void removeEntry(int modelRow) {
        entries.remove(modelRow);
    }

    public Order getEntryAtIndex(int index) {
        return entries.get(index);
    }

    public void reloadData() throws DatabaseException {
        this.entries = dataManager.getAllOrders();
    }
}
