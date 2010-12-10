package org.joshy.gfx.test.swingintegration;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.GradientFill;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Control;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 6, 2010
 * Time: 3:00:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingChartDemo implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new SwingChartDemo());
    }

    @Override
    public void run() {
        List<ElfDataPoint> data = new ArrayList<ElfDataPoint>();
        data.add(new ElfDataPoint(0,90,9));
        data.add(new ElfDataPoint(1,60,5));
        data.add(new ElfDataPoint(2,50,6));
        data.add(new ElfDataPoint(3,50,5));
        data.add(new ElfDataPoint(4,50,5));
        data.add(new ElfDataPoint(5,50,6));
        data.add(new ElfDataPoint(6,70,8));
        data.add(new ElfDataPoint(7,90,9));
        data.add(new ElfDataPoint(8,50,6));
        data.add(new ElfDataPoint(9,60,2));
        data.add(new ElfDataPoint(10,80,9));
        data.add(new ElfDataPoint(11,90,10));


        GraphControl graph = new GraphControl();
        graph.setData(data);

        JButton b1 = new JButton("asdf");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(b1,BorderLayout.WEST);
        JComponentWrapper wrapper = new JComponentWrapper();
        wrapper.setContent(graph);
        panel.add(wrapper,BorderLayout.CENTER);

        JFrame frame = new JFrame("Amino + Swing Chart Demo");
        frame.pack();
        frame.add(panel);
        frame.setSize(640,480);
        frame.setVisible(true);

        
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>() {
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(-1);
            }
        });

    }

    private class GraphControl extends Control {
        private List<ElfDataPoint> data;
        private double maxPay;

        @Override
        public void doLayout() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void doPrefLayout() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void doSkins() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void draw(GFX g) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            double colWidth = getWidth()/12-5;
            double top = 30;
            double bottom = getHeight()-30;
            double height = bottom-top;
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,getWidth(),getHeight());
            for(int month=0; month < data.size(); month++) {
                cal.set(Calendar.MONTH,month);
                ElfDataPoint d = data.get(month);
                //u.p("d = " + d);
                double x = 5+month*colWidth;
                double y = 0;
                g.translate(x,y);
                double angle = d.attitude*360/10;
                FlatColor start = FlatColor.hsb(angle,1,0.8);
                FlatColor end = FlatColor.hsb(angle,0.8,1);
                GradientFill grad = new GradientFill(start,end,0,true, 0,0,colWidth-5,0);
                g.setPaint(grad);
                g.fillRoundRect(
                        0,
                        bottom-height/maxPay*d.avgPay,
                        colWidth-5,
                        height/maxPay*d.avgPay,10,10);
                g.setPaint(FlatColor.BLACK);
                Date date = cal.getTime();
                g.drawText(sdf.format(date), Font.name("Arial").size(18).resolve(),5,bottom+15);
                g.translate(-x,-y);
            }
        }

        public void setData(List<ElfDataPoint> data) {
            this.data = data;
            for(ElfDataPoint d : data) {
                maxPay = Math.max(maxPay,d.avgPay);
            }
        }
    }

    //elf pay vs month, color elf attitude,
    public class ElfDataPoint {
        private int attitude;
        private double avgPay;
        private int month;

        public ElfDataPoint(int month, double avgPay, int attitude) {
            this.month = month;
            this.avgPay = avgPay;
            this.attitude = attitude;
        }
    }
}
