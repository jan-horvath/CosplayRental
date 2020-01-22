package cz.muni.fi.pv168.rentalapp.gui.tablemodels;

import cz.muni.fi.pv168.rentalapp.business.entities.Order;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class OrderTableModel extends AbstractTableModel {

    private enum Column {

        PRODUCT_STACKS("Products", List.class, Order::getProductStacks),
        EMAIL("Email", String.class, Order::getEmail),
        CREDIT_CARD("Credit card number", String.class, Order::getCreditCardNumber),
        FULL_NAME("Name", String.class, Order::getFullName),
        PHONE_NUMBER("Phone", String.class, Order::getPhoneNumber),
        RETURN_DATE("Return date", LocalDate.class, Order::getReturnDate);

        private <T> Column(String name, Class<T> columnClass, Function<Order, T> extractor) {
            this.name = name;
            this.columnClass = columnClass;
            this.extractor = extractor;
        }

        private final String name;
        private final Class<?> columnClass;
        private final Function<Order, ?> extractor;
    }

    private List<Order> entries;

    public OrderTableModel(List<Order> entries) {
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
        Order entry = entries.get(rowIndex);
        return Column.values()[columnIndex].extractor.apply(entry);
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
}
