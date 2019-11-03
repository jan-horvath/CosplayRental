package cz.muni.fi.pv168.cosplayrental;

import cz.muni.fi.pv168.cosplayrental.entities.Order;
import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.OrderTableModel;

import javax.swing.table.TableModel;
import java.util.List;

public class DataManager {
    private List<ProductStack> catalogue;
    private List<Order> orders;

    private CatalogueTableModel catalogueTableModel;
    private OrderTableModel orderTableModel;

    public DataManager(List<ProductStack> catalogue, List<Order> orders) {
        this.catalogue = catalogue;
        this.orders = orders;
        catalogueTableModel = new CatalogueTableModel(catalogue);
        orderTableModel = new OrderTableModel(orders);
    }

    public CatalogueTableModel getCatalogueTableModel() {
        return catalogueTableModel;
    }

    public OrderTableModel getOrderTableModel() {
        return orderTableModel;
    }
}
