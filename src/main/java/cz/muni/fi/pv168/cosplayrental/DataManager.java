package cz.muni.fi.pv168.cosplayrental;

import cz.muni.fi.pv168.cosplayrental.entities.Order;
import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.OrderTableModel;

import javax.swing.*;
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

    //public void createOrder(SortedSet<Integer>)

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

    public List<ProductStack> createOrderItems() {
        List<ProductStack> orderedItems = new ArrayList<>();

        int i = 0;
        for (Integer itemsCount : getCatalogueTableModel().piecesOrdered) {
            if (itemsCount > 0) {
                ProductStack catalogueItem = productStacks.get(i);
                ProductStack item = new ProductStack(
                        catalogueItem.getName(),
                        catalogueItem.getSize(),
                        catalogueItem.getPrice(),
                        itemsCount);
                orderedItems.add(item);
            }
            i++;
        }

        if (orderedItems.isEmpty()) {
                throw new IllegalStateException("There must be at least 1 item in the order.");
            }

        return orderedItems;
    }

    public void submitOrder() {
        Map<String, String> formData = formPanel.getFormData();
        //
        // TODO

    }
}
