import cz.muni.fi.pb168.FormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class MainWindow extends JFrame {

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
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

        // panels defined for possible future usage
        JPanel card2 = new JPanel();
        JPanel card3 = new JPanel();
        JPanel card4 = new FormPanel();

        JPanel cards = new JPanel(new CardLayout());
        cards.add(card2, "card2");
        cards.add(card3, "card3");
        cards.add(card4, "card4");
        getContentPane().add(cards);

        pack();

        catalogueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, "card2");
            }
        });

        orderCatalogueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, "card3");
            }
        });

        formButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, "card4");
            }
        });


        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                setSize(
                        Math.max(Integer.MIN_VALUE, getWidth()),
                        Math.max(700, getHeight()));
            }
        });
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