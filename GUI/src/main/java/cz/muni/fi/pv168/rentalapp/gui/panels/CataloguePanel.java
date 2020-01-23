package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.InvalidReturnDateException;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;


public class CataloguePanel extends JPanel {

    private DataManager dataManager;
    private OrderTableModel orderTM;
    private CatalogueTableModel catalogueTM;
    private JTable catalogueTable;

    private OrderOverviewPane orderOverviewPane = new OrderOverviewPane();
    private FormPanel formPanel = new FormPanel();
    private JButton createOrderButton = new JButton("Create order");

    public CataloguePanel(CatalogueTableModel ctm, OrderTableModel otm, DataManager dm) {
        setLayout(new GridLayout(1,2, 0, 0));

        dataManager = dm;
        orderTM = otm;
        catalogueTM = ctm;
        catalogueTable = new JTable(catalogueTM);
        add(new JScrollPane(catalogueTable));

        JPanel orderOverviewPanel = new JPanel();
        BoxLayout layout = new BoxLayout(orderOverviewPanel, BoxLayout.PAGE_AXIS);
        orderOverviewPanel.setLayout(layout);

        orderOverviewPanel.add(new JLabel("Order overview"));
        orderOverviewPanel.add(orderOverviewPane);
        orderOverviewPanel.add(formPanel);
        orderOverviewPanel.add(createOrderButton);

        add(orderOverviewPanel);

        addCellEditorListener();
        addCreateOrderButtonListener();
    }

    private void addCellEditorListener() {
        CellEditorListener onTableChange = new CellEditorListener() {

            public void editingStopped(ChangeEvent e) {
                orderOverviewPane.changeOrderInformation(catalogueTM.getProductStackWithSelectedPieces());
            }

            @Override
            public void editingCanceled(ChangeEvent e) {}
        };

        catalogueTable.getDefaultEditor(Integer.class).addCellEditorListener(onTableChange);
    }

    private void addCreateOrderButtonListener() {
        createOrderButton.addActionListener(e -> {
            if (catalogueTM.areAllItemsZero()) {
                JOptionPane.showMessageDialog(null, "Please select items, you wish to rent, by entering a number into the last column", "No items selected", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<Integer, Integer> productCounts = new HashMap<>();
            int catalogueTableModelRowCount = catalogueTM.getRowCount();

            for (int i = 0; i < catalogueTableModelRowCount; i++) {
                productCounts.put(catalogueTable.convertRowIndexToModel(i), (Integer) catalogueTM.getValueAt(i, 4));
            }
            try {
                dataManager.createOrderItems(formPanel.getFormData(), productCounts);
            } catch (EmptyTextboxException ETexception) {
                JOptionPane.showMessageDialog(null, "Please fill all the textfields.", "Empty textfield(s)", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (DateTimeParseException DTPexception) {
                JOptionPane.showMessageDialog(null, "Please enter the return date in the specified format (dd.MM.YYYY)", "Wrong date format", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InvalidReturnDateException IRDException) {
                JOptionPane.showMessageDialog(null, "Return date already passed. Please enter a valid one.", "Invalid return date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            orderTM.fireTableRowsInserted(0,0);
            formPanel.clearTextFields();
            catalogueTM.setAllAddToCartItemsToZero();
            orderOverviewPane.clearPane();

            JOptionPane.showMessageDialog(null, "Your order has been created!", "", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
