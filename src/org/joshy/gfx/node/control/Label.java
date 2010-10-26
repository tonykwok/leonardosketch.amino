package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
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
    private StyleInfo styleInfo;
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;

    public Label(String text) {
        this.text = text;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this);
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,text);
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        setHeight(sizeInfo.height);
    }

    @Override
    public void doLayout() {
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        boxPainter = cssSkin.createBoxPainter(this,styleInfo,sizeInfo,text,CSSSkin.State.None);
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(sizeInfo == null) {
            doPrefLayout();
        }
        boxPainter.draw(g, styleInfo, sizeInfo, this, text);
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
        if(sizeInfo == null) {
            doPrefLayout();
        }
        return styleInfo.margin.getTop() + styleInfo.borderWidth.getTop() + styleInfo.padding.getTop() + styleInfo.contentBaseline;
    }

}
