package cz.muni.fi.pb168;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FormPanel extends JPanel {

    private List<JTextField> textFields = new ArrayList<JTextField>();
    private List<JLabel> labels = new ArrayList<JLabel>();

    public FormPanel() {
        BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxlayout);

        JLabel nameLabel = new JLabel("Your name & surname:");
        labels.add(nameLabel);
        JTextField nameField = new JTextField();
        textFields.add(nameField);

        JLabel emailLabel = new JLabel("E-mail adress:");
        labels.add(emailLabel);
        JTextField emailField = new JTextField();
        textFields.add(emailField);

        JLabel cardLabel = new JLabel("Credit card number:");
        labels.add(cardLabel);
        JTextField cardField = new JTextField();
        textFields.add(cardField);

        JLabel phoneLabel = new JLabel("Phone number:");
        labels.add(phoneLabel);
        JTextField phoneField = new JTextField();
        textFields.add(phoneField);

        JButton submitButton = new JButton("SUMBIT");

        if (labels.size() != textFields.size()) System.exit(0);

        for (int i = 0; i < labels.size(); i++) {
            add(labels.get(i));
            JTextField f = textFields.get(i);
            f.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 30));
            add(f);
        }
        add(submitButton);
    }
}
