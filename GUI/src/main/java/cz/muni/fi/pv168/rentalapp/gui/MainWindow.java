package cz.muni.fi.pv168.rentalapp.gui;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.EmptyTextboxException;
import cz.muni.fi.pv168.rentalapp.business.Exceptions.InvalidReturnDateException;

import cz.muni.fi.pv168.rentalapp.business.TimeSimulator;
import cz.muni.fi.pv168.rentalapp.gui.actions.ExitAction;
import cz.muni.fi.pv168.rentalapp.gui.actions.GoToAction;
import cz.muni.fi.pv168.rentalapp.business.entities.Order;
import cz.muni.fi.pv168.rentalapp.business.entities.ProductStack;
import cz.muni.fi.pv168.rentalapp.gui.panels.CataloguePanel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.ProductStackListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;


public class MainWindow extends JFrame {

    private static List<ProductStack> CATALOG_TEST_DATA = new ArrayList<>(Arrays.asList(
            new ProductStack("Asterix helmet", ProductStack.Size.NA, 15.80, 3),
            new ProductStack("Poseidon trident", ProductStack.Size.NA, 21.90, 3),
            new ProductStack("Deadpool suit", ProductStack.Size.M,42.20, 4),
            new ProductStack("Witcher silver sword", ProductStack.Size.NA, 29, 0),
            new ProductStack("Portal gun", ProductStack.Size.NA, 42, 1),
            new ProductStack("BFG9000", ProductStack.Size.NA, 65, 0),
            new ProductStack("Ironman suit", ProductStack.Size.L, 120, 0),
            new ProductStack("Captain America suit", ProductStack.Size.L, 109, 0),
            new ProductStack("Batman suit", ProductStack.Size.S, 100, 0),
            new ProductStack("Batarang set", ProductStack.Size.NA, 25, 10)
    ));

    private static List<ProductStack> ps1 = new ArrayList<>(Arrays.asList(
            new ProductStack("Witcher silver sword", ProductStack.Size.NA, 29, 3),
            new ProductStack("Portal gun", ProductStack.Size.NA, 42, 2),
            new ProductStack("BFG9000", ProductStack.Size.NA, 65, 1)
    ));

    private static List<ProductStack> ps2 = new ArrayList<>(Arrays.asList(
            new ProductStack("Ironman suit", ProductStack.Size.L, 120, 1),
            new ProductStack("Captain America suit", ProductStack.Size.L, 109, 1)
    ));

    private static List<ProductStack> ps3 = new ArrayList<>(Arrays.asList(
            new ProductStack("Batman suit", ProductStack.Size.S, 100, 1),
            new ProductStack("Batarang set", ProductStack.Size.NA, 25, 2)
    ));


    private static List<Order> ORDER_TEST_DATA = new ArrayList<>(Arrays.asList(
            new Order(ps1, "weaponreplica@enthusiast.org", "9184345167789991", "No Name",
                    "+658291912994", LocalDate.of(2019, 12, 20)),
            new Order(ps2, "fred.kirby@gmail.org", "9184345161019991", "Fred Kirby",
                    "+929123456994", LocalDate.of(2019, 12, 21)),
            new Order(ps3, "marc.blake@batmanfan.org", "4116852067789991", "Marc Blake",
                    "+444291912994", LocalDate.of(2019, 12, 22))
    ));

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoRe: Cosplay Rental ©");

        //Initialization
        TimeSimulator timeSimulator = new TimeSimulator();
        CatalogueTableModel catalogueTableModel = new CatalogueTableModel(CATALOG_TEST_DATA);
        OrderTableModel orderTableModel = new OrderTableModel(ORDER_TEST_DATA);
        DataManager dataManager = new DataManager(CATALOG_TEST_DATA, ORDER_TEST_DATA, timeSimulator);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JLabel timeLabel = new JLabel(dateFormat.format(timeSimulator.getTime()));
        timeSimulator.addCallback(() -> {
            timeLabel.setText(dateFormat.format(timeSimulator.getTime()));
        });
        timeSimulator.addCallback(dataManager::checkReturnDates);

        JTable orderTable = new JTable(orderTableModel);
        orderTable.setDefaultRenderer(List.class, new ProductStackListRenderer());
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Cards
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);

        cards.add(new CataloguePanel(catalogueTableModel,  orderTableModel, dataManager), "Catalogue");
        cards.add(new JScrollPane(orderTable), "Orders list");


        //Bottom toolbar
        JToolBar bottomToolBar = new JToolBar();
        add(bottomToolBar, BorderLayout.AFTER_LAST_LINE);
        bottomToolBar.setVisible(false);

        JButton returnOrderButton = new JButton("Return order");
        bottomToolBar.add(returnOrderButton);

        //Top toolbar
        JToolBar topToolBar = new JToolBar();
        add(topToolBar, BorderLayout.BEFORE_FIRST_LINE);

        GoToAction gotoCatalogue = new GoToAction(() -> {
                c1.show(cards, "Catalogue");
                bottomToolBar.setVisible(false);
            }, "Catalogue", "catalogueIcon.png", KeyEvent.VK_2);

        GoToAction gotoListOrders = new GoToAction(() -> {
                c1.show(cards, "Orders list");
                bottomToolBar.setVisible(true);
                returnOrderButton.setVisible(true);
                returnOrderButton.setEnabled(true);
            }, "Orders list", "listOrdersIcon.png", KeyEvent.VK_4);

        JButton oneDayAdvanceButton = new JButton("+1 day");
        oneDayAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneDay();});
        JButton oneWeekAdvanceButton = new JButton("+1 week");
        oneWeekAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneWeek();});
        JButton fourWeeksAdvanceButton = new JButton("+4 weeks");
        fourWeeksAdvanceButton.addActionListener(e -> {timeSimulator.advanceFourWeeks();});

        returnOrderButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select order that needs to be returned.", "No order selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = orderTable.convertRowIndexToModel(selectedRow);
            dataManager.returnOrder(modelRow);
            orderTableModel.fireTableRowsDeleted(modelRow, modelRow);
            catalogueTableModel.fireTableDataChanged();
        });

        topToolBar.add(gotoCatalogue);
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
        gotoMenu.add(gotoListOrders);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(gotoMenu);

        setJMenuBar(menuBar);

        pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}