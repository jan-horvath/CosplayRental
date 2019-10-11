import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

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