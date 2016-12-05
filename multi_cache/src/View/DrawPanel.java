package View;

import Controller.CPUController;
import Model.*;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.util.Vector;


public class DrawPanel extends JPanel implements Runnable {
    //设置比例即可调整大小
    private static final int scale = 10;
    //基础图层等比例参数(勿修改)
    private static final int spaceX = 6;        //左边空隙大小
    private static final int cpuX = (20 + spaceX) * scale; //第一个cpu的x坐标
    private static final int cpuY = 6 * scale;             //第一个cpu的y坐标
    private static final int circleSize = 10 * scale;      //cpu的大小
    private static final int busX = (5 + spaceX) * scale;  //bus的x坐标
    private static final int busY = 50 * scale;            //bus的y坐标
    private static final int rectWidth = 16 * scale;       //存储空间格子的宽度
    private static final int rectHeight = 4 * scale;       //存储空间格子的高度
    private static final int cacheX = cpuX + circleSize / 2 - rectWidth / 2;    //cache的x坐标
    private static final int cacheY = 24 * scale;                       //cache的y坐标
    private static final int memoryX = (29 + spaceX) * scale;           //memory的x坐标
    private static final int memoryY = 60 * scale;         //memory的y坐标
    private static final int memoryGap = 32 * scale;       //memory的间隔
    private static final int gap = 40 * scale;             //间距
    private static final int fontSize = (int) (3.6 * scale);//字体大小
    private static final int lineSize = scale / 5;     //边线粗度
    private static final int speed = 2 * scale;            //每0.1s运动距离

    private CPUController cpuController;

    private static final int RED = 1;
    private static final int ORANGE = 2;
    private static final int PINK = 3;

    DrawPanel() {
        super();
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        this.setSize(2000, 1200);
        new Thread(this).start();
        cpuController = CPUController.getCpuController();
    }


    private void paintBackGround(Graphics2D g) {
        g.setStroke(new BasicStroke(lineSize));
        //画memory的大框
        g.setColor(Color.GREEN);
        g.fillRect(memoryX - rectWidth / 2, memoryY - (int) ((memoryY - busY) * 0.4), memoryGap * 3 + rectWidth * 2, rectHeight * 10);
        g.setColor(Color.BLACK);
        g.drawRect(memoryX - rectWidth / 2, memoryY - (int) ((memoryY - busY) * 0.4), memoryGap * 3 + rectWidth * 2, rectHeight * 10);
        //部件
        for (int i = 0; i < 4; i++) {
            //画cpu
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(cpuX + i * gap, cpuY, circleSize, circleSize);
            g.setColor(Color.BLACK);
            g.drawOval(cpuX + i * gap, cpuY, circleSize, circleSize);
            //画cache × 4
            for (int j = 0; j < 4; j++) {
                g.setColor(CPUController.getCpuController().setCacheColor(i, j));
                g.fillRect(cacheX + i * gap, cacheY + j * rectHeight, rectWidth, rectHeight);
                g.setColor(Color.BLACK);
                g.drawRect(cacheX + i * gap, cacheY + j * rectHeight, rectWidth, rectHeight);
            }
        }
        //画memory × 8
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                g.setColor(Color.YELLOW);
                g.fillRect(memoryX + memoryGap * i, memoryY + rectHeight * j, rectWidth, rectHeight);
                g.setColor(Color.BLACK);
                g.drawRect(memoryX + memoryGap * i, memoryY + rectHeight * j, rectWidth, rectHeight);
            }
        }
        //画bus
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(lineSize * 6));
        g.drawLine(busX, busY, busX + gap * 4, busY);
        //线和文字
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(lineSize));
        //画连线
        for (int i = 0; i < 4; i++) {
            //cpu到cache
            g.drawLine(cpuX + circleSize / 2 + gap * i, cpuY + circleSize, cpuX + circleSize / 2 + gap * i, cacheY);
            //cache到bus
            g.drawLine(cpuX + circleSize / 2 + gap * i, cacheY + rectHeight * 4, cpuX + circleSize / 2 + gap * i, busY - lineSize * 3);
            //bus到memory的独立垂直线
            g.drawLine(memoryX + rectWidth / 2 + memoryGap * i, busY + (int) ((memoryY - busY) * 0.8), memoryX + rectWidth / 2 + memoryGap * i, memoryY);
        }
        //bus到memory的公共垂直线
        g.drawLine(busX + gap * 2, busY + lineSize * 3, busX + gap * 2, busY + (int) ((memoryY - busY) * 0.8));
        //bus到memory的水平线
        g.drawLine(memoryX + rectWidth / 2, busY + (int) ((memoryY - busY) * 0.8), memoryX + rectWidth / 2 + memoryGap * 3, busY + (int) ((memoryY - busY) * 0.8));

        g.setFont(new Font("宋体", Font.BOLD, fontSize));
        //注明文字
        for (int i = 0; i < 4; i++) {
            //cpu号
            g.setColor(Color.BLACK);
            g.drawString("CPU " + (char) (65 + i), cpuX - fontSize * 3 + i * gap, cpuY + circleSize / 2 + fontSize / 2);
            //cache号
            g.setColor(Color.BLUE);
            g.drawString("Cache " + (char) (65 + i), cpuX + circleSize / 2 - rectWidth / 2 - fontSize * 4 + i * gap, cacheY - rectHeight / 6);
            //cache的编号
            g.setColor(Color.BLACK);
            for (int j = 0; j < 4; j++) {
                g.drawString(String.valueOf(j), cpuX + circleSize / 2 - rectWidth / 2 - fontSize + i * gap, cacheY - rectHeight / 6 + rectHeight * (j + 1));
            }
            //memory的编号
            g.setColor(Color.BLACK);
            for (int k = 0; k < 8; k++) {
                g.drawString(String.valueOf(k + i * 8), (int) (memoryX - fontSize * 1.3 + memoryGap * i), memoryY - rectHeight / 6 + rectHeight * (k + 1));
            }
        }
        //其他文字
        g.setColor(Color.BLACK);
        g.drawString("存储器", memoryX - fontSize * 6, memoryY + rectHeight * 4);

        //说明
        Color[] color = {Color.LIGHT_GRAY, Color.CYAN, Color.MAGENTA};
        String[] string = {"无效", "共享", "独占"};
        for (int i = 0; i < 3; i++) {
            g.setFont(new Font("微软雅黑", Font.BOLD, (int)(fontSize * 0.8)));
            g.drawString(string[i], memoryX + (int)(memoryGap * 0.27) + memoryGap * i, memoryY + (int)(rectHeight * 10.8));
            g.setColor(color[i]);
            g.fillRect(memoryX + memoryGap / 2 + memoryGap * i, memoryY + rectHeight * 10, rectWidth, rectHeight);
            g.setColor(Color.BLACK);
            g.drawRect(memoryX + memoryGap / 2 + memoryGap * i, memoryY + rectHeight * 10, rectWidth, rectHeight);
        }
    }

    //用来展现数据块移动的rect
    class Rect extends JPanel {
        Rect(int color) {
            this.setSize(rectWidth, rectHeight);
            switch (color) {
                case RED:
                    this.setBackground(Color.RED);
                    break;
                case ORANGE:
                    this.setBackground(Color.ORANGE);
                    break;
                case PINK:
                    this.setBackground(Color.PINK);
                    break;
                default:
                    this.setBackground(Color.GREEN);
                    break;
            }
            this.setBorder(new LineBorder(Color.BLACK));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        paintBackGround(g2);
    }

    //根据CPUid、BlockNum确定cache的位置
    //根据StorageNum确定主存块的位置
    //根据direction确定动画是由cache到memory(dir=1)还是memory到cache(dir=-1)
    private void moveRect(int CPUid, int BlockNum, int StorageNum, int direction, int color) {
        //动作数量参数
        int cacheToBus = ((busY - (cacheY + rectHeight / 2)) - (BlockNum % 4) * rectHeight) / speed;
        int onBus = (Math.abs((int) (1.5 - CPUid)) * gap + gap / 2) / speed;
        int busToMemoryVerticalCommon = (int) ((memoryY - busY) * 0.8) / speed;
        int busToMemoryHorizontal = (Math.abs((int) (1.5 - (StorageNum / 8))) * memoryGap + memoryGap / 2) / speed;
        int busToMemoryVerticalEspecial = ((StorageNum % 8 + 1) * rectHeight) / speed;
        //关于位置变化的常量
        int StartX, StartY;
        //在5种运动路径上移动的次数,当direction=1时,按数组顺序进行;当direction=-1时,按数组逆序进行
        int moveStatus[] = new int[]{cacheToBus, onBus, busToMemoryVerticalCommon, busToMemoryHorizontal, busToMemoryVerticalEspecial};
        int dx[] = new int[]{0, speed, 0, speed, 0};
        int dy[] = new int[]{speed, 0, speed, 0, speed};
        if (CPUid >= 2) {    //CPUid=2或3,direction=1时,在bus上需要左移
            dx[1] = -speed;
        }
        if (StorageNum < 16) {   //StorageNum < 16,direction=1时,在bus上需要左移
            dx[3] = -speed;
        }
        //设置起始位置
        Rect rect = new Rect(color);
        if (direction == Movement.WRITEBACK) {    //初始定位到cache
            StartX = cpuX + CPUid * gap + circleSize / 2 - rectWidth / 2;
            StartY = cacheY + BlockNum * rectHeight;
            rect.setBounds(StartX, StartY, rectWidth, rectHeight);
        } else if (direction == Movement.READIN) {    //初始定位到memory
            StartX = memoryX + (StorageNum / 8) * memoryGap;
            StartY = memoryY + StorageNum % 8 * rectHeight;
            rect.setBounds(StartX, StartY, rectWidth, rectHeight);
        } else {
            System.out.println("方向参数错误");
            return;
        }
        this.add(rect);
        //临时标记被移动的数据块的原位置
        Rect tempRect = new Rect(color);
        tempRect.setBounds(StartX, StartY, rectWidth, rectHeight);
        this.add(tempRect);
        //动画
        try {
            if (direction == Movement.WRITEBACK) {
                for (int i = 0; i <= 4; i++) {   //顺序经过5种路径
                    for (int j = 0; j < moveStatus[i]; j++) {
                        StartX += dx[i];
                        StartY += dy[i];
                        rect.setBounds(StartX, StartY, rectWidth, rectHeight);
                        Thread.sleep(100);
                    }
                }
            } else {
                for (int i = 4; i >= 0; i--) {   //反向经过5种路径
                    for (int j = 0; j < moveStatus[i]; j++) {
                        StartX += dx[i] * direction;    //运动方向也取反
                        StartY += dy[i] * direction;
                        rect.setBounds(StartX, StartY, rectWidth, rectHeight);
                        Thread.sleep(100);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {   //移除动画块
            this.remove(tempRect);
            this.remove(rect);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void Invalidate(Vector<Pair<Integer, Integer>> vector) {
        int StartX;
        int StartY;
        int EndX;
        int EndY;
        //动画
        try {
            Graphics2D g = (Graphics2D) this.getGraphics();
            for (int j = 0; j < 5; j++) {
                for (Pair<Integer, Integer> aVector : vector) {
                    StartX = cpuX + aVector.getKey() * gap + circleSize / 2 - rectWidth / 2;
                    StartY = cacheY + aVector.getValue() * rectHeight;
                    EndX = StartX + rectWidth;
                    EndY = StartY + rectHeight;
                    g.setStroke(new BasicStroke(lineSize * 2));
                    g.setColor(Color.RED);
                    g.drawLine(StartX, StartY, EndX, EndY);
                    g.drawLine(StartX, EndY, EndX, StartY);
                }
                Thread.sleep(100);
                this.repaint();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void active(Vector<Movement> moveVector, Vector<Pair<Integer, Integer>> invalidateVector) {
        if (moveVector.isEmpty() && invalidateVector.isEmpty()) {
            return;
        }
        if (!moveVector.isEmpty() && moveVector.firstElement().Direction == 0) {
            this.repaint();
            return;
        }
        Movement movement;
        int color = RED;
        for (Movement aMoveVector : moveVector) {
            movement = aMoveVector;
            moveRect(movement.CPUModelId, movement.CacheId, movement.MemoryId, movement.Direction, color++);
        }
        Invalidate(invalidateVector);
        this.repaint();

    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.active(cpuController.getMovements(), cpuController.getInvalidates());
            cpuController.clearMovementsAndInvalidates();
        }
    }
}