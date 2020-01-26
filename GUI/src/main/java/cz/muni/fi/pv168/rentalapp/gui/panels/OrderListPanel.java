package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;
import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import java.awt.*;

public class OrderListPanel extends JPanel {

    private DataManager dataManager;
    private OrderTableModel orderTM;
    private CatalogueTableModel catalogueTM;
    private JTable orderTable;

    private OrderDetailsPane orderDetailsPane = new OrderDetailsPane();
    private JButton deleteOrderButton = new JButton("Delete order");

    public OrderListPanel(CatalogueTableModel ctm, OrderTableModel otm, DataManager dm) {
        setLayout(new GridLayout(1,2, 5, 0));

        dataManager = dm;
        orderTM = otm;
        catalogueTM = ctm;
        orderTable = new JTable(orderTM);
        orderTable.removeColumn(orderTable.getColumnModel().getColumn(0));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(orderTable));

        JPanel orderDetailsPanel = new JPanel();
        BoxLayout layout = new BoxLayout(orderDetailsPanel, BoxLayout.PAGE_AXIS);
        orderDetailsPanel.setLayout(layout);

        JLabel label = new JLabel("Order details");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderDetailsPanel.add(label);
        orderDetailsPanel.add(orderDetailsPane);
        deleteOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderDetailsPanel.add(deleteOrderButton);

        add(orderDetailsPanel);

        addRowSelectionListener();
        addDeleteOrderButtonListener();
    }

    private void addRowSelectionListener() {
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            int modelRow = orderTable.convertRowIndexToModel(selectedRow);
            orderDetailsPane.displayOrderInfo(orderTM.getEntries().get(modelRow));
        });
    }

    private void addDeleteOrderButtonListener() {
        deleteOrderButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select order that needs to be returned.", "No order selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = orderTable.convertRowIndexToModel(selectedRow);

            try {
                dataManager.returnOrder(orderTM.getEntryAtIndex(modelRow).getId());
                orderTM.reloadData();
                catalogueTM.reloadData();
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
//            orderTM.removeEntry(modelRow);
            orderTM.fireTableRowsDeleted(modelRow, modelRow);

//            catalogueTM.fireTableDataChanged();
            orderTable.clearSelection();
            orderDetailsPane.clearPane();
        });
    }
}
