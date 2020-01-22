package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.entities.Order;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderListPanel extends JPanel {

    OrderTableModel orderTM;

    public OrderListPanel(List<Order> orderList) {
        setLayout(new GridLayout(1,2, 8, 0));

        orderTM = new OrderTableModel(orderList);
        add(new JTable(orderTM));

        JPanel orderDetailsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(orderDetailsPanel, BoxLayout.PAGE_AXIS);

    }
}
