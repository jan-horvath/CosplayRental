package cz.muni.fi.pv168.rentalapp.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExitAction extends AbstractAction {
    public ExitAction() {
        super("Exit", new ImageIcon(ExitAction.class.getResource("exitIcon.png")));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Exit CoReS");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }
}