package cz.muni.fi.pv168.cosplayrental;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FormPanel extends JPanel {

    public Map<String, String> formData = new HashMap<>();
    public List<JTextField> textFields = new ArrayList<JTextField>();
    private List<JLabel> labels = new ArrayList<JLabel>();


    public FormPanel() {

        BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxlayout);

        JLabel nameLabel = new JLabel("Your name & surname:");
        labels.add(nameLabel);
        JTextField nameField = new JTextField();    
        textFields.add(nameField);
        formData.put("name", nameField.getText());

        JLabel emailLabel = new JLabel("E-mail adress:");
        labels.add(emailLabel);
        JTextField emailField = new JTextField();
        textFields.add(emailField);
        formData.put("email", emailField.getText());

        JLabel cardLabel = new JLabel("Credit card number:");
        labels.add(cardLabel);
        JTextField cardField = new JTextField();
        textFields.add(cardField);
        formData.put("cardNumber", cardField.getText());

        JLabel phoneLabel = new JLabel("Phone number:");
        labels.add(phoneLabel);
        JTextField phoneField = new JTextField();
        textFields.add(phoneField);
        formData.put("phoneNumber", phoneField.getText());

        JLabel returnDateLabel = new JLabel("Return date:");
        labels.add(returnDateLabel);
        JTextField returnDateField = new JTextField();
        textFields.add(returnDateField);
        formData.put("returnDate", returnDateField.getText());


        for (int i = 0; i < labels.size(); i++) {
            add(labels.get(i));
            JTextField f = textFields.get(i);
            f.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 30));
            add(f);
        }
    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
