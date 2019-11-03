package cz.muni.fi.pv168.cosplayrental;

import com.google.common.collect.ImmutableList;
import cz.muni.fi.pv168.cosplayrental.actions.ExitAction;
import cz.muni.fi.pv168.cosplayrental.actions.GoToAction;
import cz.muni.fi.pv168.cosplayrental.entities.Order;
import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.OrderTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.ProductStackListRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainWindow extends JFrame {

    private static List<ProductStack> CATALOG_TEST_DATA = Arrays.asList(
            new ProductStack("Asterix helmet", ProductStack.Size.NA, 15.80, 3),
            new ProductStack("Poseidon trident", ProductStack.Size.NA, 21.90, 3),
            new ProductStack("Deadpool suit", ProductStack.Size.M,42.20, 4)
    );

    private static List<ProductStack> ps1 = Arrays.asList(
            new ProductStack("Witcher silver sword", ProductStack.Size.NA, 29, 3),
            new ProductStack("Portal gun", ProductStack.Size.NA, 42, 2),
            new ProductStack("BFG9000", ProductStack.Size.NA, 65, 1)
    );

    private static List<ProductStack> ps2 = Arrays.asList(
            new ProductStack("Ironman suit", ProductStack.Size.L, 120, 1),
            new ProductStack("Captain America suit", ProductStack.Size.L, 109, 1)
    );

    private static List<ProductStack> ps3 = Arrays.asList(
            new ProductStack("Batman suit", ProductStack.Size.S, 100, 1),
            new ProductStack("Batarang set", ProductStack.Size.NA, 25, 2)
    );


    private static List<Order> ORDER_TEST_DATA = Arrays.asList(
            new Order(ps1, "weaponreplica@enthusiast.org", "9184345167789991", "No Name",
                    "+658291912994", LocalDate.of(2019, 12, 20)),
            new Order(ps2, "fred.kirby@gmail.org", "9184345161019991", "Fred Kirby",
                    "+929123456994", LocalDate.of(2019, 12, 21)),
            new Order(ps3, "marc.blake@batmanfan.org", "4116852067789991", "Marc Blake",
                    "+444291912994", LocalDate.of(2019, 12, 22))
    );

    private static final JPanel BLANK = new JPanel();

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoReS: Cosplay Rental Service ©");

        //Tables and windows
        DataManager dataManager = new DataManager(CATALOG_TEST_DATA, ORDER_TEST_DATA);
        JTable catalogueTable = new JTable(dataManager.getCatalogueTableModel());
        catalogueTable.removeColumn(
                catalogueTable.getColumnModel().getColumn(CatalogueTableModel.Column.values().length)
        );

        JTable addToCartTable = new JTable(dataManager.getCatalogueTableModel());
        
        JTable orderTable = new JTable(dataManager.getOrderTableModel());
        orderTable.setDefaultRenderer(List.class, new ProductStackListRenderer());
        orderTable.setRowHeight(50);

        JButton createOrderButton = new JButton("Create order");
        createOrderButton.setVisible(false);
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);
        cards.add(new JLabel(new ImageIcon(MainWindow.class.getResource("warmup.png"))), "Home");
        cards.add(new JScrollPane(catalogueTable), "Catalogue");
        cards.add(new JScrollPane(addToCartTable), "Order");
        cards.add(new JScrollPane(new FormPanel()), "Form");
        cards.add(new JScrollPane(orderTable), "Orders list");

        add(createOrderButton, BorderLayout.PAGE_END);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                setSize(
                        Math.max(Integer.MIN_VALUE, getWidth()),
                        Math.max(700, getHeight()));
            }
        });

        //Toolbar
        JToolBar tb = new JToolBar();
        add(tb, BorderLayout.BEFORE_FIRST_LINE);

        GoToAction gotoHome = new GoToAction(() -> {
                c1.show(cards, "Home");
                createOrderButton.setVisible(false);
            }, "Home", "homeIcon.png", KeyEvent.VK_1);

        GoToAction gotoCatalogue = new GoToAction(() -> {
                c1.show(cards, "Catalogue");
                createOrderButton.setVisible(false);
            }, "Catalogue", "catalogueIcon.png", KeyEvent.VK_2);

        GoToAction gotoOrder = new GoToAction(() -> {
                c1.show(cards, "Order");
                createOrderButton.setVisible(true);
            }, "Order", "orderIcon.png", KeyEvent.VK_3);

        GoToAction gotoForm = new GoToAction(() -> {
                c1.show(cards, "Form");
                createOrderButton.setVisible(false);
            }, "Form", "formIcon.png", KeyEvent.VK_4);

        GoToAction gotoListOrders = new GoToAction(() -> {
            c1.show(cards, "Orders list");
            createOrderButton.setVisible(false);
        }, "Orders list", "listOrdersIcon.png", KeyEvent.VK_5);

        /*JButton listOrdersButton = new JButton("List orders",
                new ImageIcon(MainWindow.class.getResource("listOrdersIcon.png")));*/
        JButton customerToggleButton = new JButton("",
                new ImageIcon(MainWindow.class.getResource("customerIcon.png")));
        JButton staffToggleButton = new JButton("",
                new ImageIcon(MainWindow.class.getResource("staffIcon.png")));

        tb.add(gotoHome);
        tb.add(gotoCatalogue);
        tb.add(gotoOrder);
        tb.add(gotoForm);
        tb.add(gotoListOrders);
        gotoListOrders.setEnabled(false);
        tb.add(Box.createHorizontalGlue());
        tb.add(customerToggleButton);
        customerToggleButton.setEnabled(false);
        tb.add(staffToggleButton);

        customerToggleButton.addActionListener(e -> {
            c1.show(cards, "Home");
            gotoCatalogue.setEnabled(true);
            gotoOrder.setEnabled(true);
            gotoForm.setEnabled(true);
            staffToggleButton.setEnabled(true);

            gotoListOrders.setEnabled(false);
            customerToggleButton.setEnabled(false);
        });

        staffToggleButton.addActionListener(e -> {
            c1.show(cards, "Home");
            gotoCatalogue.setEnabled(false);
            gotoOrder.setEnabled(false);
            gotoForm.setEnabled(false);
            staffToggleButton.setEnabled(false);

            gotoListOrders.setEnabled(true);
            customerToggleButton.setEnabled(true);
        });

        //Menubar
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new ExitAction());

        JMenu gotoMenu = new JMenu("Go to");
        gotoMenu.add(gotoHome);
        gotoMenu.add(gotoCatalogue);
        gotoMenu.add(gotoOrder);
        gotoMenu.add(gotoForm);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem terms = new JMenuItem("Terms of use");
        JMenuItem contact = new JMenuItem("Contact us");
        JMenuItem howTo = new JMenuItem("How to fill in the form?");
        helpMenu.add(terms);
        helpMenu.add(contact);
        helpMenu.add(howTo);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(gotoMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
}