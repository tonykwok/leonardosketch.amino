package org.joshy.gfx.draw;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 1/15/11
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinearGradientFill extends MultiGradientFill{

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public LinearGradientFill setStartX(double startX) {
        this.startX = startX;
        return this;
    }

    public LinearGradientFill setStartY(double startY) {
        this.startY = startY;
        return this;
    }

    public LinearGradientFill setEndX(double endX) {
        this.endX = endX;
        return this;
    }

    public LinearGradientFill setEndY(double endY) {
        this.endY = endY;
        return this;
    }

    @Override
    public Paint duplicate() {
        LinearGradientFill grad = new LinearGradientFill();
        grad.startX = startX;
        grad.endX = endX;
        grad.startY = startY;
        grad.endY = endY;
        for(Stop s : stops) {
            grad.addStop(s.duplicate());
        }
        return grad;
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
