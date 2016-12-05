package View;

import Controller.CPUController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class UserPanel extends JPanel {

    UserPanel() {
        super();
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        this.setLayout(new GridLayout(1, 4));
        for (int i = 0; i < 4; i++) {
            CPURequest request = new CPURequest(i);
            this.add(request);
        }
    }

    class CPURequest extends JPanel {
        JLabel label;
        JComboBox comboBox;
        JTextField textField;
        JButton button;
        int CPUid;

        CPURequest(int num) {
            this.setLayout(new GridLayout(1, 4));

            label = new JLabel("<html><body>访问地址:<br>主存块号</body></html>");
            this.add(label);

            textField = new JTextField();
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    int keyChar = e.getKeyChar();
                    if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
                    } else {
                        e.consume();
                    }
                }
            });
            this.add(textField);

            comboBox = new JComboBox();
            comboBox.addItem("读");
            comboBox.addItem("写");
            this.add(comboBox);

            button = new JButton("↓");
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    String memoryNum = CPURequest.this.textField.getText();
                    if(Integer.parseInt(memoryNum) > 31 || Integer.parseInt(memoryNum) < 0) {
                        CPURequest.this.textField.setText("");return;
                    }
                    int type = CPURequest.this.comboBox.getSelectedIndex();
                    CPUController.getCpuController().setCode(CPUid, Integer.parseInt(memoryNum), type);
                    CPUController.getCpuController().setChanged();
                    CPUController.getCpuController().notifyObservers(CPUController.getCpuController().getCode());
                }
            });
            this.add(button);

            this.CPUid = num;
        }
    }
}

