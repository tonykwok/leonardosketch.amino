package org.joshy.gfx.css.values;

import org.joshy.gfx.draw.FlatColor;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 6, 2010
 * Time: 5:19:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShadowValue extends BaseValue {
    private int color;
    private int xoffset;
    private int yoffset;
    private int radius;
    private int spread = 0;
    private boolean inset;

    public ShadowValue(String color, String xoff, String yoff, String radius, String spread, String inset) {
        this.color = Integer.parseInt(color.substring(1),16);
        this.xoffset = Integer.parseInt(xoff.substring(0,xoff.length()-2));
        this.yoffset = Integer.parseInt(yoff.substring(0,yoff.length()-2));
        this.radius = Integer.parseInt(radius.substring(0,radius.length()-2));
        this.spread = Integer.parseInt(spread);
        this.inset = Boolean.parseBoolean(inset);
    }

    public ShadowValue(FlatColor color, int xoff, int yoff, int radius, int spread, boolean inset) {
        this.color = color.getRGBA();
        this.xoffset = xoff;
        this.yoffset = yoff;
        this.radius = radius;
        this.spread = spread;
        this.inset = inset;
    }

    @Override
    public String asString() {
        return "shadow value with stuff";
    }

    public int getXOffset() {
        return xoffset;
    }

    public int getYOffset() {
        return yoffset;
    }

    public int getRadius() {
        return radius;
    }

    public int getSpread() {
        return spread;
    }

    public int getColor() {
        return color;
    }

    public boolean isInset() {
        return inset;
    }
}
