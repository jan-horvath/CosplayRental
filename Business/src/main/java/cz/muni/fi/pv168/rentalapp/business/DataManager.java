package cz.muni.fi.pv168.rentalapp.business;

import cz.muni.fi.pv168.rentalapp.business.Exceptions.*;
import cz.muni.fi.pv168.rentalapp.database.DataSourceCreator;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.database.OrderManager;
import cz.muni.fi.pv168.rentalapp.database.ProductStackManager;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;

import javax.naming.InvalidNameException;
import javax.sql.DataSource;
import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


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
    
    public TimeSimulator getTimeSimulator() {
        return this.timeSimulator;
    }

    public Order createOrder(Map<String, String> formData, Map<Integer, Integer> productCounts) throws DatabaseException, InvalidNameException {
        checkEmptyFormData(formData);

        String fullName = formData.get("name");
        String email = formData.get("email");
        String phone = formData.get("phoneNumber");
        validateFormData(fullName, email, phone);

        LocalDate returnDate = LocalDate.parse(formData.get("returnDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (returnDate.compareTo(timeSimulator.getTime()) < 0) {
            throw new InvalidReturnDateException();
        }

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

    private void validateFormData(String fullName, String email, String phone) throws InvalidNameException {
        validateName(fullName);
        validateEmail(email);
        validatePhone(phone);
    }

    private void validateName(String fullName) throws InvalidNameException {
        String[] names = fullName.split(" ");

        int textElements = 0;
        for (int i = 0 ; i < names.length ; i++) {
            String trimmedName = names[i].trim();
            if (trimmedName.equals("")) {
                names[i] = null;
            } else {
                String nameRegex = "^[A-Z][a-z]*$";
                Pattern pat = Pattern.compile(nameRegex);
                if (!pat.matcher(trimmedName).matches()) {
                    throw new InvalidNameException();
                }
                names[i] = trimmedName;
                textElements++;
            }
        }

        if (textElements == 1) {
            throw new OneNameOnlyException();
        }

        for (String name : names) {
            if (name != null && (name.length() < 2)) {
                throw new InvalidNameException();
            }
        }
    }

    private void validateEmail(String email) {
        String trimmedEmail = email.trim();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(trimmedEmail).matches()) {
            throw new InvalidEmailAddressException();
        }
    }

    private void validatePhone(String phone) {
        String trimmedPhone = phone.trim();
        if (phone.contains("\\s")) {
            throw new WhiteCharPhoneNumberException();
        }

        String phoneRegex = "^\\+[0-9]{12}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        if (!pattern.matcher(trimmedPhone).matches()) {
            throw new InvalidPhoneNumberException();
        }
    }
}
