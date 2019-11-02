package cz.muni.fi.pv168.cosplayrental.tablemodels;

import cz.muni.fi.pv168.cosplayrental.entities.ProductStack;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.List;

public class ProductStackListRenderer extends JList<ProductStack> implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List<ProductStack> products = (List<ProductStack>) value;
        ProductStack[] productsArray = new ProductStack[products.size()];
        productsArray = products.toArray(productsArray);
        setListData(productsArray);

        table.setRowHeight(row, 20*productsArray.length);

        if (isSelected) {
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            setBackground(UIManager.getColor("Table.background"));
        }
        return this;
    }
}
