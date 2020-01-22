package cz.muni.fi.pv168.rentalapp.gui.panels;

import cz.muni.fi.pv168.rentalapp.business.entities.ProductStack;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.List;

public class OrderOverviewPane extends JEditorPane {

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public OrderOverviewPane() {
        setText("Price: 0");
    }

    public void changeOrderInformation(List<ProductStack> productStacks) {
        StringBuilder sb = new StringBuilder();

        double sum = 0.0;
        for (ProductStack ps : productStacks) {
            if (ps.getStackSize() > 0) {
                sb.append(ps.getName() + " (" + ps.getSize() + ") x" + ps.getStackSize() + "\n");
                sum += ps.getPrice() * ps.getStackSize();
            }
        }
        sb.append("Price: ").append(df2.format(sum));
        setText(sb.toString());
    }

}
