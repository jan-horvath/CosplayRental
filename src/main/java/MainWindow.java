import com.google.common.collect.ImmutableList;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    private static final List<CatalogueEntry> CATALOG_TEST_DATA = ImmutableList.of(
            new CatalogueEntry("Asterix helmet", 17.80),
            new CatalogueEntry("Poseidon trident", 21.90),
            new CatalogueEntry("Deadpool suit", 42.20)
    );

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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

        TableModel catalogTableModel = new CatalogueTableModel(CATALOG_TEST_DATA);
        JTable catalogTable = new JTable(catalogTableModel);
        catalogTable.setRowHeight(50);
        add(new JScrollPane(catalogTable));

        JButton createOrderButton = new JButton("Create order");
        createOrderButton.setPreferredSize(new Dimension(20, 30));
        add(createOrderButton, BorderLayout.PAGE_END);

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