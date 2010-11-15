package org.joshy.gfx.test.partyboard;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Nov 14, 2010
* Time: 5:58:15 PM
* To change this template use File | Settings | File Templates.
*/
class Particle extends Node {
    public double vx;
    public double vy;
    public double x;
    public double y;
    public double radius = 20;
    private FlatColor fill = FlatColor.BLACK;
    public int index;

    @Override
    public void draw(GFX g) {
        g.setPaint(fill);
        g.fillOval(x- radius,y- radius, radius *2, radius *2);
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public Node setFill(FlatColor green) {
        this.fill = green;
        return this;
    }

    public void collide(List<Particle> parts, double spring, double width, double height) {
        for(Particle p : parts) {
            double dx = p.x - x;
            double dy = p.y - y;
            double dist = Math.sqrt(dx*dx+dy*dy);
            double minDist = p.radius + radius;

            //do bounce if too close
            if(dist < minDist) {
                double angle = Math.atan2(dy,dx);
                double tx = x + Math.cos(angle)*minDist;
                double ty = y + Math.sin(angle)*minDist;
                double ax = (tx-p.x)*spring;
                double ay = (ty-p.y)*spring;

                vx -= ax;
                vy -= ay;
                p.vx += ax;
                p.vy += ay;
            }
        }
    }

    public void move(double grav, double width, double height) {
        vy += grav;
        x+=vx;
        y+=vy;
        if(x+radius > width) {
            x = width-radius;
            vx *= -0.9; //bounce and lose 10% energy
        } else if (x - radius < 0) {
            x = radius;
            vx *= -0.9;
        }

        if(y+radius > height) {
            y = height-radius;
            vy *= -0.9;
        } else if(y-radius < 0) {
            y = radius;
            vy *= -0.9;
        }

    }
    int r = 0;
    int g = 0;
    int b = 0;
    public void updateColor() {
        r = (r + 1) % 255;
        g = (g + 2) % 255;
        b = (r + g) % 255;
        fill = FlatColor.fromRGBInts(r,g,b);
    }

    public void setIndex(int index) {
        this.index = index;
        this.r = index*2;
        this.g = index*5;
        this.b = 0;
    }
}
