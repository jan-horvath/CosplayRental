package cz.muni.fi.pv168.cosplayrental;

import com.google.common.collect.ImmutableList;
import cz.muni.fi.pv168.cosplayrental.actions.ExitAction;
import cz.muni.fi.pv168.cosplayrental.actions.GoToAction;
import cz.muni.fi.pv168.cosplayrental.tableentries.CatalogueEntry;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.AddToCartTableModel;
import cz.muni.fi.pv168.cosplayrental.tablemodels.CommonTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class MainWindow extends JFrame {

    private static final List<CatalogueEntry> CATALOG_TEST_DATA = ImmutableList.of(
            new CatalogueEntry("Asterix helmet", 17.80),
            new CatalogueEntry("Poseidon trident", 21.90),
            new CatalogueEntry("Deadpool suit", 42.20)
    );
    private static final JPanel BLANK = new JPanel();

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoReS: Cosplay Rental Service ©");

        //Tables and windows
        TableModel catalogueTableModel = new CommonTableModel(CATALOG_TEST_DATA, false);
        JTable catalogueTable = new JTable(catalogueTableModel);
        catalogueTable.removeColumn(
                catalogueTable.getColumnModel().getColumn(CommonTableModel.Column.valueOf("ISADDEDTOCART").ordinal())
        );

        TableModel orderTableModel = new CommonTableModel(CATALOG_TEST_DATA, true);
        JTable orderTable = new JTable(orderTableModel);

        JButton createOrderButton = new JButton("Create order");
        createOrderButton.setVisible(false);
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);
        cards.add(new JLabel(new ImageIcon(MainWindow.class.getResource("warmup.png"))), "Home");
        cards.add(new JScrollPane(catalogueTable), "Catalogue");
        cards.add(new JScrollPane(orderTable), "Order");
        cards.add(new JScrollPane(new FormPanel()), "Form");

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

        JButton listOrdersButton = new JButton("List orders",
                new ImageIcon(MainWindow.class.getResource("listOrdersIcon.png")));
        JButton customerToggleButton = new JButton("",
                new ImageIcon(MainWindow.class.getResource("customerIcon.png")));
        JButton staffToggleButton = new JButton("",
                new ImageIcon(MainWindow.class.getResource("staffIcon.png")));

        tb.add(gotoHome);
        tb.add(gotoCatalogue);
        tb.add(gotoOrder);
        tb.add(gotoForm);
        tb.add(listOrdersButton);
        listOrdersButton.setEnabled(false);
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

            listOrdersButton.setEnabled(false);
            customerToggleButton.setEnabled(false);
        });

        staffToggleButton.addActionListener(e -> {
            c1.show(cards, "Home");
            gotoCatalogue.setEnabled(false);
            gotoOrder.setEnabled(false);
            gotoForm.setEnabled(false);
            staffToggleButton.setEnabled(false);

            listOrdersButton.setEnabled(true);
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