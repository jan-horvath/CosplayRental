package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.*;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;

import javax.naming.InvalidNameException;
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
        catalogueTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        add(new JScrollPane(catalogueTable));

        JPanel orderOverviewPanel = new JPanel();
        BoxLayout layout = new BoxLayout(orderOverviewPanel, BoxLayout.PAGE_AXIS);
        orderOverviewPanel.setLayout(layout);

        JLabel label = new JLabel("Order overview");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderOverviewPanel.add(label);
        orderOverviewPanel.add(orderOverviewPane);
        orderOverviewPanel.add(formPanel);
        createOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                Map<String, String> formData = formPanel.getFormData();
                dataManager.createOrder(formData, productCounts);
                catalogueTM.reloadData();
                orderTM.reloadData();

            } catch (EmptyTextboxException ex) {
                JOptionPane.showMessageDialog(null, "Please fill all the textfields.", "Empty textfield(s)", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (OneNameOnlyException ex) {
                JOptionPane.showMessageDialog(null, "Please enter your full name", "One-word name", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InvalidNameException ex) {
                JOptionPane.showMessageDialog(null, "Given name does not begin with capital letters or contains non alphabetic characters.", "Invalid name pattern", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (ParticularNameTooShortException ex) {
                JOptionPane.showMessageDialog(null, "Name or surname contains only one character. Please enter a valid one.", "Name/surname is too short", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InvalidEmailAddressException ex) {
                JOptionPane.showMessageDialog(null, "Given email address can't exist. Please enter a valid one.", "Invalid email addres pattern", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (WhiteCharPhoneNumberException ex) {
                JOptionPane.showMessageDialog(null, "Given phone number contains spaces or tabulators. Please delete them.", "White chars in phone number", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPhoneNumberException ex) {
                JOptionPane.showMessageDialog(null, "Given phone number does not contain 12 digits or contains non-digit characters.", "Invalid phone number", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, "Please enter the return date in the specified format (dd.MM.YYYY)", "Wrong date format", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InvalidReturnDateException ex) {
                JOptionPane.showMessageDialog(null, "Return date already passed. Please enter a valid one.", "Invalid return date", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
//            catalogueTM.fireTableDataChanged();
            orderTM.fireTableRowsInserted(0,0);
            formPanel.clearTextFields();
            catalogueTM.setAllAddToCartItemsToZero();
            orderOverviewPane.clearPane();

            JOptionPane.showMessageDialog(null, "Your order has been created!", "", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
