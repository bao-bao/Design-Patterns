import Controller.CPUController;
import View.MainFrame;

import javax.swing.*;

/**
 * Created by AMXPC on 2016/12/5.
 */
public class Main {
    public static void main(String[] args) {
        CPUController.initCPUController();
        JFrame main = new MainFrame();
    }
}
