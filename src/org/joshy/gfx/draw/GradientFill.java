package org.joshy.gfx.draw;

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
    public double startX = 0;
    public double startY = 0;
    public double endX = 0;
    public double endY = 0;
    public double angle;
    private boolean stretch;

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
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public GradientFill derive(double startX, double startY, double endX, double endY) {
        GradientFill gf = new GradientFill(start,end,angle,stretch);
        gf.startX = startX;
        gf.startY = startY;
        gf.endX = endX;
        gf.endY = endY;
        return gf;
    }

    public GradientFill derive(FlatColor start, FlatColor end) {
        return new GradientFill(start,end,this.angle,this.stretch,this.startX,this.startY,this.endX,this.endY);
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

    public GradientFill setStartColor(FlatColor flatColor) {
        this.start = flatColor;
        return this;
    }

    public GradientFill setEndColor(FlatColor flatColor) {
        this.end = flatColor;
        return this;
    }

    public boolean isStretch() {
        return stretch;
    }

    @Override
    public String toString() {
        return "GradientFill{" +
                "start=" + start +
                ", end=" + end +
                ", (" + startX +
                ", " + startY +
                ") -> (" + endX +
                ", " + endY + " )" +
                '}';
    }
}
