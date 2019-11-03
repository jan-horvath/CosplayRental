package cz.muni.fi.pv168.cosplayrental;

import cz.muni.fi.pv168.cosplayrental.entities.Order;
import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.OrderTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataManager {
    private List<ProductStack> productStacks;
    private List<Order> orders;

    private CatalogueTableModel catalogueTableModel;
    private OrderTableModel orderTableModel;
    private FormPanel formPanel;

    public DataManager(List<ProductStack> productStacks, List<Order> orders, FormPanel formPanel) {
        this.productStacks = productStacks;
        this.orders = orders;
        this.formPanel = formPanel;
        catalogueTableModel = new CatalogueTableModel(productStacks);
        orderTableModel = new OrderTableModel(orders);

    }

    public CatalogueTableModel getCatalogueTableModel() {
        return catalogueTableModel;
    }

    public OrderTableModel getOrderTableModel() {
        return orderTableModel;
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

    public List<ProductStack> createOrderItems(Map<String, String> formData) {
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
        
        String email = formData.get("email");
        String creditCardNumber = formData.get("cardNumber");
        String fullName = formData.get("name");
        String phone = formData.get("phoneNumber");
        System.out.println(email + " " + creditCardNumber + " " + fullName + " " + phone + " " + formData.get("returnDate"));
        LocalDate returnDate = LocalDate.parse(formData.get("returnDate"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Order desiredOrder = new Order(orderedItems, email, creditCardNumber, fullName, phone, returnDate);
        orders.add(desiredOrder);

        orderTableModel.fireTableRowsInserted(orders.size()-1, orders.size()-1);
        catalogueTableModel.fireTableDataChanged();

        return orderedItems;
    }
}
