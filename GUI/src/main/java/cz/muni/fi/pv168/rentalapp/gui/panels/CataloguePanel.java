package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.entities.ProductStack;
import cz.muni.fi.pv168.rentalapp.gui.FormPanel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.List;


public class CataloguePanel extends JPanel {

    CatalogueTableModel catalogueTM;
    JTable catalogueTable;
    OrderOverviewPane orderOverviewPane = new OrderOverviewPane();
    FormPanel formPanel = new FormPanel();
    JButton createOrderButton = new JButton("Create order");

    public CataloguePanel(List<ProductStack> catalogueData) {
        setLayout(new GridLayout(1,2, 0, 0));

        catalogueTM = new CatalogueTableModel(catalogueData);
        catalogueTable = new JTable(catalogueTM);
        add(catalogueTable);

        JPanel orderOverviewPanel = new JPanel();
        BoxLayout layout = new BoxLayout(orderOverviewPanel, BoxLayout.PAGE_AXIS);
        orderOverviewPanel.setLayout(layout);

        orderOverviewPanel.add(new JLabel("Order overview"));
        orderOverviewPanel.add(orderOverviewPane);
        orderOverviewPanel.add(formPanel);
        orderOverviewPanel.add(new JButton("Create order"));

        add(orderOverviewPanel);

        createCallbacks();
    }

    private void createCallbacks() {
        CellEditorListener onTableChange = new CellEditorListener() {

            public void editingStopped(ChangeEvent e) {
                System.out.println("Editing stopped");
                orderOverviewPane.changeOrderInformation(catalogueTM.getProductStackWithSelectedPieces());
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                System.out.println("Editing canceled");
            }
        };

        catalogueTable.getDefaultEditor(Integer.class).addCellEditorListener(onTableChange);
    }
}
