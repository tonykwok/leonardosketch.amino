package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

public class Textbox extends TextControl {
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private String hintText = "";

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Runnable(){
            @Override
            public void run() {
                Stage stage = Stage.createStage();
                Textbox tb = new Textbox();
                stage.setContent(tb);
                Core.getShared().getFocusManager().setFocusedNode(tb);
            }
        });
    }
    double xoff = 0;

    public Textbox() {
        setWidth(100);
        setHeight(40);
    }

    public Textbox(String text) {
        this();
        setText(text);
    }

    public Textbox setHintText(String text) {
        this.hintText = text;
        return this;
    }

    @Override
    protected double filterMouseX(double x) {
        return x - xoff - 0 - styleInfo.calcContentInsets().getLeft();
        //return x - xoff - 0 - cssSize.margin.getLeft() - cssSize.borderWidth.getLeft() - cssSize.padding.getLeft();
    }

    /* =========== Layout stuff ================= */
    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,text);

        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            sizeInfo.height = prefHeight;
        } else {
            setHeight(sizeInfo.height);
        }
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
    }

    @Override
    public void doLayout() {
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
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
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    /* =============== Drawing stuff ================ */
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

        //filter the text
        String text = filterText(getText());

        //draw the selection
        if(selection.isActive() && text.length() > 0) {
            double start = getCursor().calculateX(selection.getLeadingColumn());
            double end = getCursor().calculateX(selection.getTrailingColumn());
            g.setPaint(FlatColor.GRAY);
            g.fillRect(
                    insets.getLeft() + start + xoff,
                    insets.getTop(),
                    end-start,
                    getHeight()-insets.getTop()-insets.getBottom());
        }

        //draw the text
        Font font = getFont();
        double y = font.getAscender();
        y+=insets.getTop();
        double x = insets.getLeft();
        if(!focused && text.length() == 0) {
            g.setPaint(new FlatColor(0x6080a0));
            g.drawText(hintText, getFont(), x + xoff, y);
        }
        g.setPaint(FlatColor.BLACK);
        g.drawText(text, getFont(), x + xoff, y);

        //draw the cursor
        if(focused) {
            g.setPaint(FlatColor.BLUE);
            CursorPosition cursor = getCursor();
            double cx = cursor.calculateX();
            // draw cursor
            g.fillRect(
                    insets.getLeft()+cx, insets.getTop(),
                    1,
                    getHeight()-insets.getTop()-insets.getBottom());
        }

        //restore the old clip
        g.setClipRect(oldClip);
    }

    protected String filterText(String text) {
        return text;
    }

    @Override
    protected void processKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_ENTER) {
            ActionEvent act = new ActionEvent(ActionEvent.Action,this);
            EventBus.getSystem().publish(act);
        } else {
            super.processKeyEvent(event);
        }
    }

}
