package View;

import javax.swing.*;
import java.awt.*;

/**
 * Created by AMXPC on 2016/12/5.
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        super();
        this.setTitle("main");
        this.setSize(1800, 1200);
        this.setLayout(new BorderLayout());
        UserPanel conPanel = new UserPanel();
        DrawPanel drawPanel = new DrawPanel();
        this.add(conPanel, BorderLayout.NORTH);
        this.add(drawPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
