package cz.muni.fi.pv168.rentalapp.gui.renderers;

import cz.muni.fi.pv168.rentalapp.business.TimeSimulator;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReturnDatesRenderer extends JLabel implements TableCellRenderer {

    private TimeSimulator timeSimulator;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ReturnDatesRenderer(TimeSimulator timeSimulator) {
        this.timeSimulator = timeSimulator;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        LocalDate returnDate = LocalDate.parse((String) value, formatter);
        if (returnDate.isBefore(timeSimulator.getTime())) {
            setBackground(new Color(255, 50, 50));
        } else {
            setBackground(Color.WHITE);
        }
        setText(value.toString());
        return this;
    }
}
