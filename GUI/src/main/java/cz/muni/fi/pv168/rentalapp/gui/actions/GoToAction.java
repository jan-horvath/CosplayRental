package cz.muni.fi.pv168.rentalapp.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GoToAction extends AbstractAction {

    private final Runnable goToOperation;

    public GoToAction(Runnable goToOperation, String name, String iconFileName, int acceleratorKey) {
        super(name, new ImageIcon(GoToAction.class.getResource(iconFileName)));
        this.goToOperation = goToOperation;
        putValue(SHORT_DESCRIPTION, "Go to " + name + " page");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(acceleratorKey, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        goToOperation.run();
    }
}
