
package de.cookiemc.application.gui.panels;

import java.awt.*;
import javax.swing.*;

import de.cookiemc.application.gui.Application;

public class ModulesPanel extends JPanel {


    private final JLabel comingSoon;

    public ModulesPanel() {
        comingSoon = new JLabel();
        comingSoon.setText("Coming soon!");
        comingSoon.setForeground(Color.RED);
        comingSoon.setFont(new Font("Arial", Font.BOLD, 30));

        int x = (int) (Application.getInstance().getBounds().getWidth() / 2);
        int y = (int) (Application.getInstance().getBounds().getHeight() / 2);

        comingSoon.setBounds(x, y, 300, 100);

        add(comingSoon);
    }
}
