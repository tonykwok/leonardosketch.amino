package org.joshy.gfx.node.control;

import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public class Textbox extends TextControl {
    double xoff = 0;
    private CSSSkin.BoxState cssSize;

    public Textbox() {
        setWidth(100);
        setHeight(40);
    }

    public Textbox(String text) {
        this();
        setText(text);
    }

    @Override
    protected double filterMouseX(double x) {
        return x - xoff - 0 - cssSize.margin.getLeft() - cssSize.borderWidth.getLeft() - cssSize.padding.getLeft();
    }

    @Override
    public void doSkins() {
        super.doSkins();
        setLayoutDirty();
    }

    /* =========== Layout stuff ================= */
    @Override
    public void doPrefLayout() {
        cssSize = cssSkin.getSize(this,text);
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            cssSize.width = prefWidth;
        } else {
            setWidth(cssSize.width);
        }
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            cssSize.height = prefHeight;
        } else {
            setHeight(cssSize.height);
        }
    }

    @Override
    public void doLayout() {
        if(cssSize == null) doPrefLayout();
        cssSize.width = getWidth();
    }

    @Override
    public double getBaseline() {
        return cssSize.margin.getTop() + cssSize.borderWidth.getTop() + cssSize.padding.getTop() + cssSize.contentBaseline;
    }

    @Override
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    /* =============== Drawing stuff ================ */
    @Override
    public void draw(GFX g) {

        //draw the background and border first
        if(cssSize == null) {
            this.doPrefLayout();
        }
        CSSMatcher matcher = new CSSMatcher(this);
        cssSkin.drawBackground(g, matcher,"", new Bounds(0,0,getWidth(),getHeight()));
        cssSkin.drawBorder(g,matcher,"",new Bounds(0,0,getWidth(),getHeight()));

        double left = cssSize.margin.getLeft()+cssSize.borderWidth.getLeft()+cssSize.padding.getLeft();
        double right = cssSize.padding.getRight()+cssSize.borderWidth.getRight()+cssSize.margin.getRight();
        //set a new clip
        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(
                cssSize.margin.getLeft()+cssSize.borderWidth.getLeft(),
                0,
                width - left - right,
                height));

        //adjust x to scroll if needed
        CursorPoint cursor = getCurrentCursorPoint();
        if(cursor.cursorX < -xoff) {
            xoff = 0-cursor.cursorX + 10;
        }
        if(cursor.cursorX + xoff > (width-left-right)-10) {
            xoff = (width-left-right)-cursor.cursorX - 10;
        }
        if(cursor.cursorX == 0) {
            xoff = 0;
        }

        //filter the text
        String text = filterText(getText());

        Font font = this.getFont();
        font = cssSkin.getDefaultFont();

        //draw the selection
        if(selection.isActive() && text.length() >= 1) {
            CursorPoint cp = getCurrentCursorPoint();
            double start = font.getWidth(text.substring(0,selection.getLeadingColumn()));
            double end = font.getWidth(text.substring(0,selection.getTrailingColumn()));
            g.setPaint(FlatColor.GRAY);
            g.fillRect(
                    cssSize.margin.getLeft() + cssSize.borderWidth.getLeft() + start + xoff,
                    cp.cursorY + 2 + cssSize.margin.getTop() + cssSize.borderWidth.getTop() + cssSize.padding.getTop(),
                    end-start,
                    cp.cursorH);
            g.setPaint(FlatColor.BLACK);
        }

        //draw the text
        g.setPaint(FlatColor.BLACK);
        g.drawText(text, font,
                cssSize.margin.getLeft() + cssSize.borderWidth.getLeft() + cssSize.padding.getLeft() + xoff,
                getBaseline());

        //draw the cursor
        g.setPaint(FlatColor.BLUE);
        if(focused) {
            CursorPoint cp = getCurrentCursorPoint();
            // draw cursor
            g.fillRect(
                    cssSize.margin.getLeft() + cssSize.borderWidth.getLeft() + + cssSize.padding.getLeft() + cp.cursorX + xoff,
                    cssSize.margin.getTop() + cssSize.borderWidth.getTop() + cssSize.padding.getTop() + cp.cursorY + 2,
                    cp.cursorW, cp.cursorH);
        }

        //restore the old clip
        g.setClipRect(oldClip);
    }

    protected String filterText(String text) {
        return text;
    }

}
