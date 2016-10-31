import javax.swing.*;
import java.awt.*;

/**
 * Created by ocean on 16-7-3.
 */

public class MainFrame extends JFrame {
    MainFrame() {
        super();
        this.setTitle("main");
        this.setSize(1800, 1200);
        this.setLayout(new BorderLayout());
        UserPanel conPanel = new UserPanel();
        DrawPanel drawPanel = new DrawPanel(conPanel);
        this.add(conPanel, BorderLayout.NORTH);
        this.add(drawPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
