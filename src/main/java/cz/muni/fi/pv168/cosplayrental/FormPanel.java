package cz.muni.fi.pv168.cosplayrental;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FormPanel extends JPanel {

    public List<JTextField> textFields = new ArrayList<>();
    private List<JLabel> labels = new ArrayList<>();


    public FormPanel() {

        BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxlayout);

        labels.add(new JLabel("Your name & surname:"));
        JTextField nameField = new JTextField();
        textFields.add(nameField);

        labels.add(new JLabel("E-mail adress:"));
        JTextField emailField = new JTextField();
        textFields.add(emailField);

        labels.add(new JLabel("Credit card number:"));
        JTextField cardField = new JTextField();
        textFields.add(cardField);

        labels.add(new JLabel("Phone number:"));
        JTextField phoneField = new JTextField();
        textFields.add(phoneField);

        labels.add(new JLabel("Return date (dd.MM.yyyy):"));
        JTextField returnDateField = new JTextField();
        textFields.add(returnDateField);


        for (int i = 0; i < labels.size(); i++) {
            add(labels.get(i));
            JTextField f = textFields.get(i);
            f.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 30));
            add(f);
        }
    }

    public void clearTextFields() {
        for (JTextField field : textFields) {
            field.setText("");
        }
    }

    public Map<String, String> getFormData() {
        Map<String, String> formData = new HashMap<>();
        formData.put("name", textFields.get(0).getText());
        formData.put("email", textFields.get(1).getText());
        formData.put("cardNumber", textFields.get(2).getText());
        formData.put("phoneNumber", textFields.get(3).getText());
        formData.put("returnDate", textFields.get(4).getText());
        return formData;
    }
}
