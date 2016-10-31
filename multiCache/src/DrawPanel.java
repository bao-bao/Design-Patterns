import javafx.scene.paint.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;

/**
 * Created by ocean on 16-7-3.
 */

public class DrawPanel extends JPanel implements Runnable {
    private int cpuX = 260; //第一个cpu的x坐标
    private int cpuY = 60;             //第一个cpu的y坐标
    private int circleSize = 100;      //cpu的大小
    private int busX = 110;  //bus的x坐标
    private int busY = 500;            //bus的y坐标
    private int rectWidth = 160;       //存储空间格子的宽度
    private int rectHeight = 40;      //存储空间格子的高度
    private int cacheX = 230;
    private int cacheY = 240;
    private int memoryX = 350;
    private int memoryY = 600;
    private int memoryGap = 320;
    private int gap = 400;
    private int fontSize = 40;
    private int lineSize = 2;
    private int speed = 20;
    //记录cache状态的数组
    private int cacheStatus[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    //运动方向常量
    private int STOC = -1;
    private int CTOS = 1;

    private UserPanel userPanel;


    DrawPanel(UserPanel userPanel) {
        super();
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        this.setSize(2000,1200);
        this.userPanel = userPanel;
        new Thread(this).start();
    }

    private void paintBackGround(Graphics2D g) {
        g.setStroke(new BasicStroke(lineSize));
        //画memory的大方框
        g.setColor(Color.GREEN);
        g.fillRect(memoryX - rectWidth / 2, memoryY - (int)((memoryY - busY) * 0.4), memoryGap * 3 + rectWidth * 2, rectHeight * 10);
        g.setColor(Color.BLACK);
        g.drawRect(memoryX - rectWidth / 2, memoryY - (int)((memoryY - busY) * 0.4), memoryGap * 3 + rectWidth * 2, rectHeight * 10);
        //部件
        for(int i = 0; i < 4; i++) {
            //画cpu
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(cpuX + i * gap, cpuY, circleSize, circleSize);
            g.setColor(Color.BLACK);
            g.drawOval(cpuX + i * gap, cpuY, circleSize, circleSize);
            //画cache × 4
            for(int j = 0; j < 4; j++) {
                g.setColor(mapColor(cacheStatus[i * 4 + j]));
                g.fillRect(cacheX + i * gap, cacheY + j * rectHeight, rectWidth, rectHeight);
                g.setColor(Color.BLACK);
                g.drawRect(cacheX + i * gap, cacheY + j * rectHeight, rectWidth, rectHeight);
            }
        }
        //画memory × 8
        for(int i = 0; i < 4; i++) {
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
        for(int i = 0; i < 4; i++) {
            //cpu到cache
            g.drawLine(cpuX + circleSize / 2 + gap * i, cpuY + circleSize, cpuX + circleSize / 2 + gap * i, cacheY);
            //cache到bus
            g.drawLine(cpuX + circleSize / 2 + gap * i, cacheY + rectHeight * 4, cpuX + circleSize / 2 + gap * i, busY - lineSize * 3);
            //bus到memory的独立垂直线
            g.drawLine(memoryX + rectWidth / 2 + memoryGap * i, busY + (int)((memoryY - busY) * 0.8), memoryX + rectWidth / 2 + memoryGap * i, memoryY);
        }
        //bus到memory的公共垂直线
        g.drawLine(busX + gap * 2, busY + lineSize * 3, busX + gap * 2, busY + (int)((memoryY - busY) * 0.8));
        //bus到memory的水平线
        g.drawLine(memoryX + rectWidth / 2, busY + (int)((memoryY - busY) * 0.8), memoryX + rectWidth / 2 + memoryGap * 3, busY + (int)((memoryY - busY) * 0.8));

        g.setFont(new Font("宋体", Font.BOLD, fontSize));
        //注明文字
        for(int i = 0; i < 4; i++) {
            //cpu号
            g.setColor(Color.BLACK);
            g.drawString("CPU " + (char)(65 + i), cpuX - fontSize * 3 + i * gap, cpuY + circleSize / 2 + fontSize / 2);
            //cache号
            g.setColor(Color.BLUE);
            g.drawString("Cache " + (char)(65 + i), cpuX + circleSize / 2 - rectWidth / 2 - fontSize * 4 + i * gap, cacheY - rectHeight / 6);
            //cache的编号
            g.setColor(Color.BLACK);
            for(int j = 0; j < 4; j++) {
                g.drawString(String.valueOf(j), cpuX + circleSize / 2 - rectWidth / 2 - fontSize + i * gap, cacheY - rectHeight / 6 + rectHeight * (j + 1));
            }
            //memory的编号
            g.setColor(Color.BLACK);
            for(int k = 0; k < 8; k++) {
                g.drawString(String.valueOf(k + i * 8), (int)(memoryX - fontSize * 1.3 + memoryGap * i), memoryY - rectHeight / 6 + rectHeight * (k + 1));
            }
        }
        //其他文字
        g.setColor(Color.BLACK);
        g.drawString("存储器", memoryX - fontSize * 6, memoryY + rectHeight * 4);
    }
//
//    private void paintMoveRectangle(Graphics g, int CPUid, int StorageNum, int BlockNum, int direction) {
//        Graphics2D g2 = (Graphics2D) g;
//        Stroke stroke = new BasicStroke(lineSize);
//        g2.setStroke(stroke);
//        //动作数量参数
//        int cacheToBus = ((busY - (cacheY + rectHeight / 2)) - (BlockNum % 4) * rectHeight) / speed;
//        int onBus = (Math.abs((int)(1.5 - CPUid)) * gap + gap / 2) / speed;
//        int busToMemoryVerticalCommon = (int)((memoryY - busY) * 0.8) / speed;
//        int busToMemoryHorizontal = (Math.abs((int)(1.5 - CPUid)) * memoryGap + memoryGap / 2) / speed;
//        int busToMemoryVerticalEspecial = ((StorageNum % 8 + 1) * rectHeight) / speed;
//        try {
//            if(direction == 1) {
//                g2.setColor(Color.RED);
//                g2.fillRect(cpuX + CPUid * gap + circleSize / 2 - rectWidth / 2, cacheY + BlockNum * rectHeight, rectWidth, rectHeight);
//                g2.setColor(Color.BLACK);
//                g2.drawRect(cpuX + CPUid * gap + circleSize / 2 - rectWidth / 2, cacheY + BlockNum * rectHeight, rectWidth, rectHeight);
//                for(int i = 0; i < cacheToBus; i++) {
//
//                }
//
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private class Rect extends JPanel {
        Rect() {
            this.setSize(rectWidth, rectHeight);
            this.setBackground(Color.RED);
            this.setBorder(new LineBorder(Color.BLACK));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        paintBackGround(g2);
    }

    private void moveRect(int CPUid, int BlockNum, int StorageNum, int direction) {
        //动作数量参数
        int cacheToBus = ((busY - (cacheY + rectHeight / 2)) - (BlockNum % 4) * rectHeight) / speed;
        int onBus = (Math.abs((int)(1.5 - CPUid)) * gap + gap / 2) / speed;
        int busToMemoryVerticalCommon = (int)((memoryY - busY) * 0.8) / speed;
        int busToMemoryHorizontal = (Math.abs((int)(1.5 - (StorageNum / 8))) * memoryGap + memoryGap / 2) / speed;
        int busToMemoryVerticalEspecial = ((StorageNum % 8 + 1) * rectHeight) / speed;
        //关于位置变化的常量
        int StartX, StartY;
        //在5种运动路径上移动的次数,当direction=1时,按数组顺序进行;当direction=-1时,按数组逆序进行
        int moveStatus[] = new int[]{cacheToBus, onBus, busToMemoryVerticalCommon, busToMemoryHorizontal, busToMemoryVerticalEspecial};
        int dx[] = new int[]{0, speed, 0, speed, 0};
        int dy[] = new int[]{speed, 0, speed, 0, speed};
        if(CPUid >= 2) {    //CPUid=2或3,direction=1时,在bus上需要左移
            dx[1] = -speed;
        }
        if(StorageNum < 16) {   //StorageNum < 16,direction=1时,在bus上需要左移
            dx[3] = -speed;
        }
        //设置起始位置
        Rect rect = new Rect();
        if(direction == CTOS) {    //初始定位到cache
            StartX = cpuX + CPUid * gap + circleSize / 2 - rectWidth / 2;
            StartY = cacheY + BlockNum * rectHeight;
            rect.setBounds(StartX, StartY, rectWidth, rectHeight);
        }
        else if(direction == STOC) {    //初始定位到memory
            StartX = memoryX + (StorageNum / 8) * memoryGap;
            StartY = memoryY + StorageNum % 8 * rectHeight;
            rect.setBounds(StartX, StartY, rectWidth, rectHeight);
        }
        else {
            System.out.println("方向参数错误");
            return;
        }
        this.add(rect);
        //临时标记被移动的数据块的原位置
        Rect tempRect = new Rect();
        tempRect.setBounds(StartX, StartY, rectWidth, rectHeight);
        this.add(tempRect);
        //动画
        try {
            if(direction == CTOS) {
                for(int i = 0; i <= 4; i++) {   //顺序经过5种路径
                    for(int j = 0; j < moveStatus[i]; j++) {
                        StartX += dx[i];
                        StartY += dy[i];
                        rect.setBounds(StartX, StartY, rectWidth, rectHeight);
                        Thread.sleep(100);
                    }
                }
            }
            else {
                for(int i = 4; i >= 0; i--) {   //反向经过5种路径
                    for(int j = 0; j < moveStatus[i]; j++) {
                        StartX += dx[i] * direction;    //运动方向也取反
                        StartY += dy[i] * direction;
                        rect.setBounds(StartX, StartY, rectWidth, rectHeight);
                        Thread.sleep(100);
                    }
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {   //移除动画块
            this.remove(tempRect);
            this.remove(rect);
            this.repaint();
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void Invalidate(int BlockNum, boolean[] CPUStatus) {
        int StartX;
        int StartY;
        int EndX;
        int EndY;
        //动画
        try {
            Graphics2D g = (Graphics2D)this.getGraphics();
                for (int j = 0; j < 5; j++) {
                    for(int i = 0; i < 4; i++) {
                    if(CPUStatus[i]) {
                        StartX = cpuX + i * gap + circleSize / 2 - rectWidth / 2;
                        StartY = cacheY + BlockNum * rectHeight;
                        EndX = StartX + rectWidth;
                        EndY = StartY + rectHeight;
                        g.setStroke(new BasicStroke(lineSize * 2));
                        g.setColor(Color.RED);
                        g.drawLine(StartX, StartY, EndX, EndY);
                        g.drawLine(StartX, EndY, EndX, StartY);
                    }
                }
                Thread.sleep(100);
                repaint();
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void RdMiss(int CPUid, int StorageNum) {
//        moveRect(CPUid, StorageNum % 4, StorageNum, 1);
//    }
//
//    private void WtMiss(int CPUid, int StorageNum) {
//        moveRect(CPUid, StorageNum % 4, StorageNum, 1);
//    }

    private void active(Code code) {

        if(code.visitCPU == code.visitAddBlockID && code.visitCPU == code.codeType && code.visitCPU == 0) {
            return;
        }

        MonitorBtoF[] process = MonitorProcess.getMonitor().getProcess();
        if(process[code.visitCPU - 1].BusWireStatus == 1) {     //RdMiss
            int isWrite = -1;
            for(int i = 0; i < 4; i++) {
                if(process[i].CtSStorageNum != -1) {
                    isWrite = i;
                }
            }
            if(isWrite == -1) {
                moveRect(code.visitCPU - 1, process[0].BlockNum, code.visitAddBlockID, STOC);
            }
            else {
                moveRect(process[isWrite].CPUid - 1, process[isWrite].BlockNum, code.visitAddBlockID, CTOS);
                cacheStatus[(process[isWrite].CPUid - 1) * 4 + process[isWrite].BlockNum] = process[isWrite].BlockStatus;
                repaint();
                moveRect(code.visitCPU - 1, process[0].BlockNum, code.visitAddBlockID, STOC);
            }
        }
        else if(process[code.visitCPU - 1].BusWireStatus == 2) {    //WrMiss
            int isWrite = -1;
            for(int i = 0; i < 4; i++) {
                if(process[i].CtSStorageNum != -1) {
                    isWrite = i;
                }
            }
            if(isWrite == -1) {
                moveRect(code.visitCPU - 1, process[0].BlockNum, code.visitAddBlockID, STOC);
            }
            else {
                moveRect(process[isWrite].CPUid - 1, process[isWrite].BlockNum, code.visitAddBlockID, CTOS);
                moveRect(code.visitCPU - 1, process[0].BlockNum, code.visitAddBlockID, STOC);
            }
            //作废cache
            boolean invalidate[] = {false, false, false, false};
            int needInvalidate = -1;
            for(int i = 0; i < 4; i++) {
                if(process[i].BusWireStatus == 5) {
                    invalidate[i] = true;
                    needInvalidate = i;
                }
            }
            if(needInvalidate != -1) {
                Invalidate(process[needInvalidate].BlockNum, invalidate);
            }
        }
        else if(process[code.visitCPU - 1].BusWireStatus == 3) {    //Read

        }
        else if(process[code.visitCPU - 1].BusWireStatus == 4) {    //Write
            //作废cache
            boolean invalidate[] = {false, false, false, false};
            int needInvalidate = -1;
            for(int i = 0; i < 4; i++) {
                if(process[i].BusWireStatus == 5) {
                    invalidate[i] = true;
                    needInvalidate = i;
                }
            }
            if(needInvalidate != -1) {
                Invalidate(process[needInvalidate].BlockNum, invalidate);
            }
        }
        else {}
        //重绘背景
        for(int i = 0; i < 4; i++) {
            cacheStatus[(process[i].CPUid - 1) * 4 + process[i].BlockNum] = process[i].BlockStatus;
        }
        this.repaint();

    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.active(userPanel.getCode());
            userPanel.resetCode();
        }
    }

    public java.awt.Color mapColor(int i) {
        if(i == 1)
            return Color.LIGHT_GRAY;
        else if(i == 2)
            return Color.CYAN;
        else if(i == 3)
            return Color.MAGENTA;
        return Color.GRAY;
    }

}
