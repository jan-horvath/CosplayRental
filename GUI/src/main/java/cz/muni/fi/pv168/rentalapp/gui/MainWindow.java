package cz.muni.fi.pv168.rentalapp.gui;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.InvalidReturnDateException;

import cz.muni.fi.pv168.rentalapp.business.TimeSimulator;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.gui.actions.ExitAction;
import cz.muni.fi.pv168.rentalapp.gui.actions.GoToAction;
import cz.muni.fi.pv168.rentalapp.database.entities.Order;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.ProductStackListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;


public class MainWindow extends JFrame {

    public MainWindow() throws IOException, DatabaseException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoRe: Cosplay Rental ©");

        TimeSimulator timeSimulator = new TimeSimulator();
        FormPanel formPanel = new FormPanel();

        //Tables
        DataManager dataManager = new DataManager(timeSimulator);

        CatalogueTableModel catalogueTableModel = new CatalogueTableModel(dataManager.getAllCatalogueData());
        OrderTableModel orderTableModel = new OrderTableModel(dataManager.getAllOrders());
        JTable catalogueTable = new JTable(catalogueTableModel);

        catalogueTable.removeColumn(
                catalogueTable.getColumnModel().getColumn(CatalogueTableModel.Column.values().length)
        );

        JTable addToCartTable = new JTable(catalogueTableModel);

        JTable orderTable = new JTable(orderTableModel);
        orderTable.setDefaultRenderer(List.class, new ProductStackListRenderer());
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Cards/Windows
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);

        cards.add(new JScrollPane(catalogueTable), "Catalogue");
        cards.add(new JScrollPane(addToCartTable), "Order");
        cards.add(new JScrollPane(formPanel), "Form");
        cards.add(new JScrollPane(orderTable), "Orders list");

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                setSize(
                        Math.max(Integer.MIN_VALUE, getWidth()),
                        Math.max(700, getHeight()));
            }
        });

        //Bottom toolbar
        JToolBar bottomToolBar = new JToolBar();
        add(bottomToolBar, BorderLayout.AFTER_LAST_LINE);
        bottomToolBar.setVisible(false);

        JButton createOrderButton = new JButton("Create order");
        JButton submitOrderButton = new JButton("Submit order");
        JButton returnOrderButton = new JButton("Return order");
        bottomToolBar.add(createOrderButton);
        bottomToolBar.add(submitOrderButton);
        bottomToolBar.add(Box.createHorizontalGlue());
        bottomToolBar.add(returnOrderButton);

        //Top toolbar
        JToolBar topToolBar = new JToolBar();
        add(topToolBar, BorderLayout.BEFORE_FIRST_LINE);

        GoToAction gotoCatalogue = new GoToAction(() -> {
                c1.show(cards, "Catalogue");
                bottomToolBar.setVisible(false);
            }, "Catalogue", "catalogueIcon.png", KeyEvent.VK_2);

        GoToAction gotoOrder = new GoToAction(() -> {
                c1.show(cards, "Order");
                bottomToolBar.setVisible(true);
                createOrderButton.setVisible(true);
                createOrderButton.setEnabled(true);
                submitOrderButton.setVisible(true);
                submitOrderButton.setEnabled(false);
                returnOrderButton.setVisible(false);
            }, "Order", "orderIcon.png", KeyEvent.VK_3);

        GoToAction gotoListOrders = new GoToAction(() -> {
                c1.show(cards, "Orders list");
                bottomToolBar.setVisible(true);
                createOrderButton.setVisible(false);
                submitOrderButton.setVisible(false);
                returnOrderButton.setVisible(true);
                returnOrderButton.setEnabled(true);
            }, "Orders list", "listOrdersIcon.png", KeyEvent.VK_4);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JLabel timeLabel = new JLabel(dateFormat.format(timeSimulator.getTime()));
        JButton oneDayAdvanceButton = new JButton("+1 day");
        oneDayAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneDay();});
        JButton oneWeekAdvanceButton = new JButton("+1 week");
        oneWeekAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneWeek();});
        JButton fourWeeksAdvanceButton = new JButton("+4 weeks");
        fourWeeksAdvanceButton.addActionListener(e -> {timeSimulator.advanceFourWeeks();});

        timeSimulator.addCallback(() -> {
            timeLabel.setText(dateFormat.format(timeSimulator.getTime()));
        });

        timeSimulator.addCallback(() -> {
            try {
                dataManager.checkReturnDates();
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        });


        returnOrderButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select order that needs to be returned.", "No order selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = orderTable.convertRowIndexToModel(selectedRow);
            try {
                dataManager.returnOrder(orderTableModel.getEntryAtIndex(modelRow).getId());
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
            orderTableModel.removeEntry(modelRow);
            orderTableModel.fireTableRowsDeleted(modelRow, modelRow);
            catalogueTableModel.updateAvailableItems(dataManager.getAllCatalogueData());
            catalogueTableModel.fireTableDataChanged();
        });

        createOrderButton.addActionListener( e -> {
            CatalogueTableModel c = (CatalogueTableModel) catalogueTable.getModel();
            if (c.areAllItemsZero()) {
                JOptionPane.showMessageDialog(null, "Please select items, you wish to rent, by entering a number into the last column", "No items selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            bottomToolBar.setVisible(true);
            createOrderButton.setVisible(true);
            createOrderButton.setEnabled(false);
            submitOrderButton.setVisible(true);
            submitOrderButton.setEnabled(true);
            returnOrderButton.setVisible(false);
            c1.show(cards, "Form");
        });

        submitOrderButton.addActionListener( e -> {
            Map<Integer, Integer> productCounts = new HashMap<>();
            Order order = null;
            int catalogueTableModelRowCount = catalogueTableModel.getRowCount();
            for (int i = 0; i < catalogueTableModelRowCount; i++) {
                productCounts.put(catalogueTable.convertRowIndexToModel(i), (Integer) catalogueTableModel.getValueAt(i, 4));
            }
            try {
                order = dataManager.createOrder(formPanel.getFormData(), productCounts);
            } catch (EmptyTextboxException ETexception) {
                JOptionPane.showMessageDialog(null, "Please fill all the textfields.", "Empty textfield(s)", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (DateTimeParseException DTPexception) {
                JOptionPane.showMessageDialog(null, "Please enter the return date in the specified format (dd.MM.YYYY)", "Wrong date format", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InvalidReturnDateException IRDException) {
                JOptionPane.showMessageDialog(null, "Return date already passed. Please enter a valid one.", "Invalid return date", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
            if (order != null) {
                orderTableModel.addOrderToEntries(order);
            }
            catalogueTableModel.updateAvailableItems(dataManager.getAllCatalogueData());
            catalogueTableModel.fireTableDataChanged();
            orderTableModel.fireTableRowsInserted(0,0);
            formPanel.clearTextFields();
            bottomToolBar.setVisible(false);
            JOptionPane.showMessageDialog(null, "Your order has been created!", "", JOptionPane.INFORMATION_MESSAGE);
        });

        topToolBar.add(gotoCatalogue);
        topToolBar.add(gotoOrder);
        topToolBar.add(gotoListOrders);
        topToolBar.add(Box.createHorizontalGlue());
        topToolBar.add(timeLabel);
        topToolBar.add(Box.createHorizontalGlue());
        topToolBar.add(oneDayAdvanceButton);
        topToolBar.add(oneWeekAdvanceButton);
        topToolBar.add(fourWeeksAdvanceButton);

        //Menubar
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new ExitAction());

        JMenu gotoMenu = new JMenu("Go to");
        gotoMenu.add(gotoCatalogue);
        gotoMenu.add(gotoOrder);
        gotoMenu.add(gotoListOrders);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(gotoMenu);

        setJMenuBar(menuBar);

        pack();
    }

    public static void main(String[] args) {
        // try - catch because of calling DataManager that calls OrderManager and DataSourceCreator which throws IOExc
        EventQueue.invokeLater(() -> {
            try {
                new MainWindow().setVisible(true);
            } catch (IOException | DatabaseException e) {
                e.printStackTrace();
            }
        });
    }
}