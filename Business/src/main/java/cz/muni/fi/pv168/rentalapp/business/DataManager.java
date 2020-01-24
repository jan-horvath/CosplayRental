package cz.muni.fi.pv168.rentalapp.business;

import cz.muni.fi.pv168.rentalapp.business.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.InvalidReturnDateException;
import cz.muni.fi.pv168.rentalapp.database.DataSourceCreator;
import cz.muni.fi.pv168.rentalapp.database.OrderManager;
import cz.muni.fi.pv168.rentalapp.database.ProductStackManager;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;

import javax.sql.DataSource;
import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataManager {
    private List<ProductStack> productStacks;
    private List<Order> orders;
    private OrderManager orderManager;

    private TimeSimulator timeSimulator;

    public DataManager(List<ProductStack> productStacks, List<Order> orders, TimeSimulator timeSimulator) throws IOException {
        this.productStacks = productStacks;
        this.orders = orders;
        this.timeSimulator = timeSimulator;
        // DB version: productStack and orders arguments do not exist at this point, must be loaded from database
        //      - load all Orders, all StoreProductStacks
        // DataManager passes loaded data to MainWindow, MainWindow visualizes them

        DataSource dataSource = DataSourceCreator.getDataSource();
        this.orderManager = new OrderManager(dataSource);
    }

    public void createOrder(Map<String, String> formData, Map<Integer, Integer> productCounts) {
        checkEmptyFormData(formData);

        LocalDate returnDate = LocalDate.parse(formData.get("returnDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (returnDate.compareTo(timeSimulator.getTime()) < 0) {
            throw new InvalidReturnDateException();
        }

        String email = formData.get("email");
        String creditCardNumber = formData.get("cardNumber");
        String fullName = formData.get("name");
        String phone = formData.get("phoneNumber");

        List<ProductStack> orderedItems = createOrderItems(productCounts);
        Order desiredOrder = new Order(orderedItems, email, fullName, phone, returnDate);
        // insert desiredOrder into ORDERS, desiredOrder obtains id (call setId() in OrderManager)
        // insert orderedItems into ORDEREDPS using orderId that was assigned during order insertion into ORDERS
        //          storeId should be assigned to catalogue product stacks from the point of loading DB into memory
        orders.add(desiredOrder);
    }

    private void checkEmptyFormData(Map<String, String> formData) {
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getValue().isEmpty()) {
                throw new EmptyTextboxException();
            }
        }
    }

    private List<ProductStack> createOrderItems(Map<Integer, Integer> productCounts) {
        List<ProductStack> orderedItems = new ArrayList<>();

        for (Map.Entry<Integer, Integer> productCount : productCounts.entrySet()) {
            Integer stackSize = productCount.getValue();
            if (stackSize > 0) {
                ProductStack wantsToOrder = productStacks.get(productCount.getKey());
                wantsToOrder.setStackSize(wantsToOrder.getStackSize() - stackSize);
                orderedItems.add(new ProductStack(
                        wantsToOrder.getName(), wantsToOrder.getSize(), wantsToOrder.getPrice(), stackSize));
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
//        OrderManager.deleteOrder(orderToRemove.getId());
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
