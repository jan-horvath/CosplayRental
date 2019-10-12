import com.google.common.collect.ImmutableList;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    private static final List<CatalogEntry> CATALOG_TEST_DATA = ImmutableList.of(
            new CatalogEntry("Asterix helmet", 17.80),
            new CatalogEntry("Poseidon trident", 21.90),
            new CatalogEntry("Deadpool suit", 42.20)
    );

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuCatalogue = new JMenu("Catalogue");
        JMenu menuOrderCatalogue = new JMenu("Order catalogue");
        JMenu menuForm = new JMenu("Form");

        menuBar.add(menuCatalogue);
        menuBar.add(menuOrderCatalogue);
        menuBar.add(menuForm);

        setJMenuBar(menuBar);
        // I wanted to put an image into the main window just to entertain you, but after half an hour of
        // finding the simple and elegant solution I gave it up, sorry. Maybe the next time. Good night!

        TableModel catalogTableModel = new CatalogTableModel(CATALOG_TEST_DATA);
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