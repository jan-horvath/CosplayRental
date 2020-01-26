package cz.muni.fi.pv168.rentalapp.business;

import cz.muni.fi.pv168.rentalapp.business.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.InvalidReturnDateException;
import cz.muni.fi.pv168.rentalapp.database.DataSourceCreator;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
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
    private ProductStackManager productStackManager;

    private TimeSimulator timeSimulator;

    public DataManager(List<ProductStack> productStacks, List<Order> orders, TimeSimulator timeSimulator) throws IOException, DatabaseException {
        this(timeSimulator);

        this.productStacks = productStacks;
        this.orders = orders;
    }

    public DataManager(TimeSimulator timeSimulator) throws IOException, DatabaseException {
        this.timeSimulator = timeSimulator;
        DataSource dataSource = DataSourceCreator.getDataSource();
        this.orderManager = new OrderManager(dataSource);
        this.productStackManager = new ProductStackManager(dataSource);
    }

    public List<ProductStack> getAllCatalogueData() {
        return productStackManager.getAllStoreProductStacks();
    }

    public List<Order> getAllOrders() throws DatabaseException {
        return orderManager.getAllOrders();
    }


    public Order createOrder(Map<String, String> formData, Map<Integer, Integer> productCounts) throws DatabaseException {
        checkEmptyFormData(formData);

        LocalDate returnDate = LocalDate.parse(formData.get("returnDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (returnDate.compareTo(timeSimulator.getTime()) < 0) {
            throw new InvalidReturnDateException();
        }

        String email = formData.get("email");
        String fullName = formData.get("name");
        String phone = formData.get("phoneNumber");

        List<ProductStack> orderedItems = createOrderItems(productCounts);
        return orderManager.insertOrder(orderedItems, email, fullName, phone, returnDate);
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

        for (Map.Entry<Integer, Integer> entry : productCounts.entrySet()) {
            Integer orderedStackSize = entry.getValue();
            if (orderedStackSize > 0) {
                ProductStack storePS = productStackManager.getStoreProductStackById(entry.getKey()+1);
                storePS.setStackSize(storePS.getStackSize() - orderedStackSize);
                productStackManager.updateStoreProductStack(storePS);
                // assigned id will be overwritten at the point of storing orderedPS into orderedProductStacks table
                // add new constructor with storeID only?
                orderedItems.add(new ProductStack(1, storePS.getId(),
                        storePS.getName(), storePS.getSize(), storePS.getPrice(), orderedStackSize));
            }
        }
        return orderedItems;
    }

    public void returnOrder(long orderId) throws DatabaseException {
        Order orderToRemove = orderManager.getOrderById(orderId);

        for (ProductStack returnPS : orderToRemove.getProductStacks()) {
            long storeID = returnPS.getStoreId();
            ProductStack storePS = productStackManager.getStoreProductStackById(storeID);
            storePS.setStackSize(storePS.getStackSize() + returnPS.getStackSize());
            productStackManager.updateStoreProductStack(storePS);
        }
        orderManager.deleteOrder(orderId);
    }

    public void checkReturnDates() throws DatabaseException {
        int notReturnedOrders = 0;
        String message = "Customers that did not keep the return date (name, delay, ordered items):";

        for (Order order : orderManager.getAllOrders()) {
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
