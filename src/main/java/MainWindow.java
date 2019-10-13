import cz.muni.fi.pb168.FormPanel;

import com.google.common.collect.ImmutableList;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

        JMenuBar menuBar = new JMenuBar();
        JMenu menuCatalogue = new JMenu("Catalogue");
        JMenu menuOrderCatalogue = new JMenu("Order catalogue");
        JMenu menuForm = new JMenu("Form");
        JMenu menuHelp = new JMenu("Help");

        JMenuItem terms = new JMenuItem("Terms of use");
        JMenuItem contact = new JMenuItem("Contact us");
        JMenuItem howTo = new JMenuItem("How to fill in the form?");

        menuBar.add(menuCatalogue);
        menuBar.add(menuOrderCatalogue);
        menuBar.add(menuForm);
        menuBar.add(menuHelp);

        menuHelp.add(terms);
        menuHelp.add(contact);
        menuForm.add(howTo);

        setJMenuBar(menuBar);

        JToolBar tb = new JToolBar();
        JButton homeButton = new JButton("HomePage",
                new ImageIcon(MainWindow.class.getResource("homeIcon.png")));
        JButton catalogueButton = new JButton("Catalogue",
                new ImageIcon(MainWindow.class.getResource("catalogueIcon.png")));
        JButton orderCatalogueButton = new JButton("Order Catalogue",
                new ImageIcon(MainWindow.class.getResource("orderCatalogueIcon.png")));
        JButton formButton = new JButton("Form",
                new ImageIcon(MainWindow.class.getResource("formIcon.png")));

        tb.add(homeButton);
        tb.add(catalogueButton);
        tb.add(orderCatalogueButton);
        tb.add(formButton);

        add(tb, BorderLayout.BEFORE_FIRST_LINE);

        TableModel catalogueTableModel = new CatalogueTableModel(CATALOG_TEST_DATA);
        JTable catalogueTable = new JTable(catalogueTableModel);

        TableModel orderTableModel = new OrderTableModel(CATALOG_TEST_DATA);
        JTable orderTable = new JTable(orderTableModel);

        JButton createOrderButton = new JButton("Create order");
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);
        cards.add(new JScrollPane(BLANK), "Home");
        cards.add(new JScrollPane(catalogueTable), "Catalogue");
        cards.add(new JScrollPane(orderTable), "Order");
        cards.add(new JScrollPane(new FormPanel()), "Form");

        add(createOrderButton, BorderLayout.PAGE_END);

        ActionListener goToHomepage = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c1.show(cards, "Home");
            }
        };

        ActionListener goToCatalogue = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c1.show(cards, "Catalogue");
                createOrderButton.setVisible(true);
            }
        };

        ActionListener goToOrder = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c1.show(cards, "Order");
                createOrderButton.setVisible(false);
            }
        };

        ActionListener goToForm = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c1.show(cards, "Form");
                createOrderButton.setVisible(false);
            }
        };

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                setSize(
                        Math.max(Integer.MIN_VALUE, getWidth()),
                        Math.max(700, getHeight()));
            }
        });

        homeButton.addActionListener(goToHomepage);
        catalogueButton.addActionListener(goToCatalogue);
        orderCatalogueButton.addActionListener(goToOrder);
        createOrderButton.addActionListener((goToOrder));
        formButton.addActionListener(goToForm);

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