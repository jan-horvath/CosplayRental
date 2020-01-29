package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.database.entities.ProductStack;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.List;

public class OrderOverviewPane extends JEditorPane {

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public OrderOverviewPane() {
        setText("Price: 0");
        setEditable(false);
    }

    public void clearPane() {
        setText("Price: 0");
    }

    public void changeOrderInformation(List<ProductStack> productStacks) {
        StringBuilder sb = new StringBuilder();

        double sum = 0.0;

        for (ProductStack ps : productStacks) {
            if (ps.getStackSize() > 0) {
                sum += ps.getPrice() * ps.getStackSize();
            }
        }

        sb.append("Price: ").append(df2.format(sum)).append("\n\n");

        for (ProductStack ps : productStacks) {
            if (ps.getStackSize() > 0) {
                sb.append(ps.getName()).append(" (").append(ps.getSize()).append(") x").append(ps.getStackSize())
                        .append("\n");
            }
        }

        setText(sb.toString());
    }

}
