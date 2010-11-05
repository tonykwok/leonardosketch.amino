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
        double y = 0;
        double x = insets.getLeft();
        g.setPaint(FlatColor.BLACK);
        for(int row = 0; row < _layout_model.lineCount(); row++) {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            //draw the selection part of the line
            if(selection.isActive()) {
                int[] startRC = getCursor().indexToRowCol(selection.getLeadingColumn());
                int[] endRC = getCursor().indexToRowCol(selection.getTrailingColumn());
                double start = getCursor().calculateX(startRC[0],startRC[1]);
                double end = getCursor().calculateX(endRC[0],endRC[1]);
                //if all on same line
                if(startRC[0] == endRC[0] && startRC[0] == row) {
                    //draw normal strip
                    drawSelectionStrip(g, insets,font,start,end,y);
                } else {
                    //start strip
                    if(row == startRC[0]) {
                        drawSelectionStrip(g,insets,font,start,getWidth()-insets.getRight(),y);
                    }
                    //middle strip
                    if(row > startRC[0] && row < endRC[0]) {
                        drawSelectionStrip(g,insets,font,0,getWidth()-insets.getRight(),y);
                    }
                    //end strip
                    if(row == endRC[0]) {
                        drawSelectionStrip(g,insets,font,0,end,y);
                    }
                }
            }
            g.setPaint(FlatColor.BLACK);
            g.drawText(line.getString(), font, x, insets.getTop()+y+font.getAscender());
            y += line.getHeight();
        }

        //draw the cursor
        if(isFocused()) {
            g.setPaint(FlatColor.BLUE);
            CursorPosition cursor = getCursor();
            double cx = cursor.calculateX();
            double cy = cursor.calculateY();
            // draw cursor
            g.fillRect(
                    insets.getLeft()+cx,
                    insets.getTop()+cy,
                    1,
                    font.getAscender()+font.getDescender());
        }


        //restore the old clip
        g.setClipRect(oldClip);
    }

    private void drawSelectionStrip(GFX g, Insets insets, Font font, double start, double end, double y) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(
                insets.getLeft() + start,
                insets.getTop()+y,
                end-start,
                font.getAscender()+font.getDescender());
    }

}
