package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 25, 2010
 * Time: 5:50:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Label extends Control {
    private String text = "Label";
    private Font font;
    private double baseline;
    private FlatColor fill;
    private CSSSkin.BoxState size;
    private BoxPainter boxPainter;

    public Label(String text) {
        this.fill = FlatColor.BLACK;
        this.text = text;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        size = cssSkin.getSize(this,text);
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            size.width = prefWidth;
        } else {
            setWidth(size.width);
        }
        setHeight(size.height);
        boxPainter = cssSkin.createBoxPainter(this,size,text,CSSSkin.State.None);
    }

    @Override
    public void doLayout() {
        if(size != null) {
            size.width = getWidth();
            size.height = getHeight();
        }
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(cssSkin != null) {
            if(size == null) {
                doPrefLayout();
            }
            boxPainter.draw(g, size, this, text);
        }
    }

    public void setText(String text) {
        this.text = text;
        setLayoutDirty();
        setDrawingDirty();
    }

    @Override
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public double getBaseline() {
        if(size == null) {
            doPrefLayout();
        }
        return size.margin.getTop() + size.borderWidth.getTop() + size.padding.getTop() + size.contentBaseline;
    }

    public Label setFill(FlatColor fill) {
        this.fill = fill;
        return this;
    }
}
