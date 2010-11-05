package org.joshy.gfx.node.control;

import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;

public class Textarea extends TextControl {
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;

    public Textarea() {
        setWidth(100);
        setHeight(100);
        this.allowMultiLine = true;
    }

    public Textarea(String text) {
        this();
        setText(text);
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
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            sizeInfo.height = prefHeight;
        } else {
            setHeight(sizeInfo.height);
        }
    }

    @Override
    public void doLayout() {
        double h = getHeight();
        Insets insets = styleInfo.calcContentInsets();
        double w = getWidth()-insets.getLeft()-insets.getRight();
        layoutText(w,sizeInfo.contentHeight);
        setHeight(h);
        sizeInfo.width = getWidth();
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        CSSSkin.State state = CSSSkin.State.None;
        if(isFocused()) {
            state = CSSSkin.State.Focused;
        }        
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, text, state);
    }

    @Override
    public double getBaseline() {
        return styleInfo.calcContentInsets().getTop()+ styleInfo.contentBaseline;
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        //draw background and border, but skip the text
        boxPainter.draw(g, styleInfo, sizeInfo, this, "");

        Insets insets = styleInfo.calcContentInsets();
        //set a new clip
        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(
                styleInfo.margin.getLeft()+styleInfo.borderWidth.getLeft(),
                0,
                width - insets.getLeft() - insets.getRight(),
                height));

        //draw the text
        Font font = getFont();
        double y = font.getAscender();
        y+=insets.getTop();
        double x = insets.getLeft();
        g.setPaint(FlatColor.BLACK);
        for(TextLayoutModel.LayoutLine line :_layout_model.lines()) {
            g.drawText(line.getString(), font, x, y);
            y += line.getHeight();
        }

        //restore the old clip
        g.setClipRect(oldClip);
    }

}
