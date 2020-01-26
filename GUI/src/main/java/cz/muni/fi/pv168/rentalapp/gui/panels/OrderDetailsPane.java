package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.entities.Order;
import cz.muni.fi.pv168.rentalapp.business.entities.ProductStack;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class OrderDetailsPane extends JEditorPane {

    public OrderDetailsPane() {
        setEditable(false);
    }

    public void clearPane() {
        StringBuilder sb = new StringBuilder();

        sb.append("Full name: \n");
        sb.append("Email: \n");
        sb.append("Phone number: \n");
        sb.append("Credit card number: \n");
        sb.append("Return date: \n");

        setText(sb.toString());
    }

    public void displayOrderInfo(Order order) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        sb.append("Full name: ").append(order.getFullName()).append("\n");
        sb.append("Email: ").append(order.getEmail()).append("\n");
        sb.append("Phone number: ").append(order.getPhoneNumber()).append("\n");
        sb.append("Credit card number: ").append(order.getCreditCardNumber()).append("\n");
        sb.append("Return date: ").append(order.getReturnDate().format(dtf)).append("\n\n").append("Items:\n");

        for (ProductStack ps : order.getProductStacks()) {
            sb.append(ps.getName()).append("(").append(ps.getSize()).append(") x").append(ps.getStackSize()).append("\n");
        }

        setText(sb.toString());
    }
}
