package cz.muni.fi.pv168.cosplayrental;

import cz.muni.fi.pv168.cosplayrental.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.cosplayrental.Exceptions.InvalidReturnDateException;
import cz.muni.fi.pv168.cosplayrental.entities.Order;
import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.OrderTableModel;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataManager {
    private List<ProductStack> productStacks;
    private List<Order> orders;

    private CatalogueTableModel catalogueTableModel;
    private OrderTableModel orderTableModel;
    private FormPanel formPanel;

    private TimeSimulator timeSimulator;

    public DataManager(List<ProductStack> productStacks, List<Order> orders, FormPanel formPanel, TimeSimulator timeSimulator) {
        this.productStacks = productStacks;
        this.orders = orders;
        this.formPanel = formPanel;
        catalogueTableModel = new CatalogueTableModel(productStacks);
        orderTableModel = new OrderTableModel(orders);
        this.timeSimulator = timeSimulator;

    }

    public CatalogueTableModel getCatalogueTableModel() {
        return catalogueTableModel;
    }

    public OrderTableModel getOrderTableModel() {
        return orderTableModel;
    }

    public void createOrder(Map<String, String> formData) {
        checkEmptyFormData(formData);

        LocalDate returnDate = LocalDate.parse(formData.get("returnDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (returnDate.compareTo(timeSimulator.getTime()) < 0) {
            throw new InvalidReturnDateException();
        }

        List<ProductStack> orderedItems = createOrderItems();
        
        String email = formData.get("email");
        String creditCardNumber = formData.get("cardNumber");
        String fullName = formData.get("name");
        String phone = formData.get("phoneNumber");

        Order desiredOrder = new Order(orderedItems, email, creditCardNumber, fullName, phone, returnDate);
        orders.add(desiredOrder);

        orderTableModel.fireTableRowsInserted(orders.size()-1, orders.size()-1);
        catalogueTableModel.fireTableDataChanged();
    }

    private void checkEmptyFormData(Map<String, String> formData) {
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getValue().isEmpty()) {
                throw new EmptyTextboxException();
            }
        }
    }

    private List<ProductStack> createOrderItems() {
        List<ProductStack> orderedItems = new ArrayList<>();

        for (int i = 0; i < catalogueTableModel.getRowCount(); i++) {
            int itemCount = (int) catalogueTableModel.getValueAt(i,
                    catalogueTableModel.getColumnCount()-1);
            if (itemCount > 0) {
                ProductStack wantsToOrder = catalogueTableModel.getOrderedProductStack(i);
                wantsToOrder.setStackSize(wantsToOrder.getStackSize() - itemCount);
                orderedItems.add(new ProductStack(
                        wantsToOrder.getName(), wantsToOrder.getSize(), wantsToOrder.getPrice(), itemCount));
            }
        }
        return orderedItems;
    }

    public void returnOrder(int orderIndex) {
        Order orderToRemove = orders.get(orderIndex);

        for (ProductStack returnPS : orderToRemove.getProductStacks()) {
            boolean wasFound = false;

            for (ProductStack ps : productStacks) {
                if (returnPS.equals(ps)) {
                    wasFound = true;
                    ps.setStackSize(ps.getStackSize() + returnPS.getStackSize());
                    break;
                }
            }

            if (!wasFound) {
                throw new IllegalStateException("Product stack with name " + returnPS.getName()
                        + " (" + returnPS.getSize() + "): was not found in the database.");
            }
        }

        orders.remove(orderIndex);
        orderTableModel.fireTableRowsDeleted(orderIndex, orderIndex);
        catalogueTableModel.fireTableDataChanged();
    }

    public void checkReturnDates() {
        int notReturnedOrders = 0;
        String message = "Customers that did not keep the return date (name, delay, ordered items):";

        for (Order order : orders) {
            long differenceInDays = ChronoUnit.DAYS.between(order.getReturnDate(), timeSimulator.getTime());

            if (differenceInDays > 0) {
                if (differenceInDays == 3 || differenceInDays % 7 == 0) {
                    if (differenceInDays == 3) {
                        System.err.println(order.getFullName() + "'s order is 3 days past its return date. Notification email has been sent to " + order.getEmail());
                    } else {
                        System.err.println(order.getFullName() + "'s order is " + differenceInDays/7 + " week(s) past its return date. Notification email has been sent to " + order.getEmail());
                    }
                    message +=  createNotReturnedOrderCustomerData(order, differenceInDays);
                    notReturnedOrders++;
                }
            }
        }
        if (notReturnedOrders > 0) {
            JOptionPane.showMessageDialog(null, message, "Not Returned Orders", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private String createNotReturnedOrderCustomerData(Order order, long differenceInDays) {
        String notReturnedItems = "";
        for (ProductStack ps : order.getProductStacks()) {
            notReturnedItems += "\n    " + ps.getName() + ", " + ps.getStackSize() + " piece(s)";
        }
        return "\n" + order.getFullName() + " (" + differenceInDays + " days)" + ":" + notReturnedItems;
    }
}
