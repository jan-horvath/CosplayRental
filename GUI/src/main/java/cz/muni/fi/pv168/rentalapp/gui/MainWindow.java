package cz.muni.fi.pv168.rentalapp.gui;

import cz.muni.fi.pv168.rentalapp.business.DataManager;
import cz.muni.fi.pv168.rentalapp.business.TimeSimulator;
import cz.muni.fi.pv168.rentalapp.database.DatabaseException;
import cz.muni.fi.pv168.rentalapp.gui.actions.ExitAction;
import cz.muni.fi.pv168.rentalapp.gui.actions.GoToAction;
import cz.muni.fi.pv168.rentalapp.gui.panels.CataloguePanel;
import cz.muni.fi.pv168.rentalapp.gui.panels.OrderListPanel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.CatalogueTableModel;
import cz.muni.fi.pv168.rentalapp.gui.tablemodels.OrderTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MainWindow extends JFrame {
    public MainWindow() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
            IllegalAccessException, IOException, DatabaseException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoRe: Cosplay Rental ©");

        UIManager.LookAndFeelInfo lookAndFeel = UIManager.getInstalledLookAndFeels()[1];
        System.out.println(lookAndFeel.getClassName());
        UIManager.setLookAndFeel(lookAndFeel.getClassName());

        //Initialization
        TimeSimulator timeSimulator = new TimeSimulator();
        DataManager dataManager = new DataManager(timeSimulator);
        CatalogueTableModel catalogueTableModel = new CatalogueTableModel(dataManager);
        OrderTableModel orderTableModel = new OrderTableModel(dataManager);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JLabel timeLabel = new JLabel(dateFormat.format(timeSimulator.getTime()));
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

        //Cards
        CardLayout c1 = new CardLayout();
        JPanel cards = new JPanel(c1);
        add(cards);

        cards.add(new CataloguePanel(catalogueTableModel,  orderTableModel, dataManager), "Catalogue");
        cards.add(new OrderListPanel(catalogueTableModel, orderTableModel, dataManager), "Orders list");

        //Actions
        GoToAction gotoCatalogue = new GoToAction(() -> {
                c1.show(cards, "Catalogue");
            }, "Catalogue", "catalogueIcon.png", KeyEvent.VK_2);

        GoToAction gotoListOrders = new GoToAction(() -> {
                c1.show(cards, "Orders list");
            }, "Existing orders", "listOrdersIcon.png", KeyEvent.VK_4);

        JButton oneDayAdvanceButton = new JButton("+1 day");
        oneDayAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneDay();});
        JButton oneWeekAdvanceButton = new JButton("+1 week");
        oneWeekAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneWeek();});
        JButton fourWeeksAdvanceButton = new JButton("+4 weeks");
        fourWeeksAdvanceButton.addActionListener(e -> {timeSimulator.advanceFourWeeks();});

        //Top toolbar
        JToolBar topToolBar = new JToolBar();
        add(topToolBar, BorderLayout.BEFORE_FIRST_LINE);
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

//        pack();
        setSize(1000, 500);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new MainWindow().setVisible(true);
            } catch (ClassNotFoundException
                    | IOException
                    | DatabaseException
                    | IllegalAccessException
                    | InstantiationException
                    | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }
}