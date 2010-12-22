package org.joshy.gfx.draw;

import org.joshy.gfx.util.u;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: May 5, 2010
* Time: 8:21:28 PM
* To change this template use File | Settings | File Templates.
*/
public class GradientFill implements Paint {
    public FlatColor start;
    public FlatColor end;
    private double startX = 0;
    private double startY = 0;
    private double endX = 0;
    private double endY = 0;
    public double angle;
    private boolean stretch;
    private boolean startSnapped = true;
    private boolean endSnapped = true;

    public GradientFill() {

    }
    public GradientFill(FlatColor start, FlatColor end, double angle, boolean stretch) {
        this(start,end,angle,stretch,0,0,0,0);
    }

    public GradientFill(FlatColor start, FlatColor end, double angle, boolean stretch, double startX, double startY, double endX, double endY) {
        this.start = start;
        this.end = end;
        this.angle = angle;
        this.stretch = stretch;
        this.setStartX(startX);
        this.setStartY(startY);
        this.setEndX(endX);
        this.setEndY(endY);
    }

    public GradientFill derive(double startX, double startY, double endX, double endY) {
        GradientFill gf = new GradientFill(start,end,angle,stretch);
        gf.setStartX(startX);
        gf.setStartY(startY);
        gf.setEndX(endX);
        gf.setEndY(endY);
        gf.startSnapped = startSnapped;
        gf.endSnapped = endSnapped;
        return gf;
    }

    public GradientFill derive(FlatColor start, FlatColor end) {
        return new GradientFill(start,end,this.angle,this.stretch, this.getStartX(), this.getStartY(), this.getEndX(), this.getEndY());
    }

    public GradientFill setStartX(double startX) {
        this.startX = startX;
        return this;
    }

    public GradientFill setEndX(double endX) {
        this.endX = endX;
        return this;
    }

    public GradientFill setStartY(double startY) {
        this.startY = startY;
        return this;
    }

    public GradientFill setEndY(double endY) {
        this.endY = endY;
        return this;
    }

    public FlatColor getStartColor() {
        return start;
    }
    public GradientFill setStartColor(FlatColor flatColor) {
        this.start = flatColor;
        return this;
    }

    public FlatColor getEndColor() {
        return end;
    }
    public GradientFill setEndColor(FlatColor flatColor) {
        this.end = flatColor;
        return this;
    }

    public boolean isStretch() {
        return stretch;
    }

    public boolean isStartSnapped() {
        return startSnapped;
    }

    @Override
    public String toString() {
        return "GradientFill{" +
                ""+Integer.toHexString(start.getRGBA()) +
                " -> " + Integer.toHexString(end.getRGBA()) +
                ", (" + getStartX() +
                ", " + getStartY() +
                ") -> (" + getEndX() +
                ", " + getEndY() + " )" +
                '}';
    }

    public void setStartSnapped(boolean startSnapped) {
        this.startSnapped = startSnapped;
    }

    public boolean isEndSnapped() {
        return endSnapped;
    }

    public void setEndSnapped(boolean endSnapped) {
        this.endSnapped = endSnapped;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }
}
