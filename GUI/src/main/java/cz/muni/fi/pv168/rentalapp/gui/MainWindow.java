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

    private TimeSimulator timeSimulator;
    private DataManager dataManager;
    private CatalogueTableModel catalogueTableModel;
    private OrderTableModel orderTableModel;

    private GoToAction gotoCatalogue;
    private GoToAction gotoListOrders;

    private CardLayout cards;
    private JPanel mainPanel;
    private JToolBar toolBar;

    private void initialize() throws DatabaseException, IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CoRe: Cosplay Rental ©");

        timeSimulator = new TimeSimulator();
        dataManager = new DataManager(timeSimulator);
        catalogueTableModel = new CatalogueTableModel(dataManager);
        orderTableModel = new OrderTableModel(dataManager);
    }

    private void setLookAndFeel(int i) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.LookAndFeelInfo lookAndFeel = UIManager.getInstalledLookAndFeels()[1];
        UIManager.setLookAndFeel(lookAndFeel.getClassName());
    }

    private void createMainPanel() {
        cards = new CardLayout();
        mainPanel = new JPanel(cards);

        add(mainPanel);
        mainPanel.add(new CataloguePanel(catalogueTableModel,  orderTableModel, dataManager), "Catalogue");
        mainPanel.add(new OrderListPanel(catalogueTableModel, orderTableModel, dataManager), "Orders list");
    }

    private void createGoToActions() {
        gotoCatalogue = new GoToAction(() -> {
            cards.show(mainPanel, "Catalogue");
        }, "Catalogue", "catalogueIcon.png", KeyEvent.VK_2);

        gotoListOrders = new GoToAction(() -> {
            cards.show(mainPanel, "Orders list");
        }, "Existing orders", "listOrdersIcon.png", KeyEvent.VK_4);
    }

    private void createTimeLabel() throws DatabaseException {
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

        toolBar.add(timeLabel);
    }

    private void createTimeManipulatingButtons() {
        JButton oneDayAdvanceButton = new JButton("+1 day");
        oneDayAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneDay();});
        JButton oneWeekAdvanceButton = new JButton("+1 week");
        oneWeekAdvanceButton.addActionListener(e -> {timeSimulator.advanceOneWeek();});
        JButton fourWeeksAdvanceButton = new JButton("+4 weeks");
        fourWeeksAdvanceButton.addActionListener(e -> {timeSimulator.advanceFourWeeks();});

        toolBar.add(oneDayAdvanceButton);
        toolBar.add(oneWeekAdvanceButton);
        toolBar.add(fourWeeksAdvanceButton);
    }

    private void createToolbar() throws DatabaseException {
        toolBar = new JToolBar();

        toolBar.add(new JButton(gotoCatalogue));
        toolBar.add(new JButton(gotoListOrders));
        toolBar.add(Box.createHorizontalGlue());
        createTimeLabel();
        toolBar.add(Box.createHorizontalGlue());
        createTimeManipulatingButtons();

        add(toolBar, BorderLayout.BEFORE_FIRST_LINE);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new ExitAction());

        JMenu gotoMenu = new JMenu("Go to");
        gotoMenu.add(gotoCatalogue);
        gotoMenu.add(gotoListOrders);

        menuBar.add(fileMenu);
        menuBar.add(gotoMenu);

        setJMenuBar(menuBar);
    }

    public MainWindow() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
            IllegalAccessException, IOException, DatabaseException {

        initialize();

        setLookAndFeel(1);

        createMainPanel();

        createGoToActions();

        createToolbar();

        createMenuBar();

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