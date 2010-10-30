package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> lines = new ArrayList<String>();

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
        layoutText();
        setHeight(lines.size()*Font.DEFAULT.calculateHeight("ASDF"));
    }

    @Override
    public void doLayout() {
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        boxPainter = cssSkin.createBoxPainter(this,styleInfo,sizeInfo,text,CSSSkin.State.None);
    }

    //TODO: all of this code is very very dodgy and must be rewritten
    private void layoutText() {
        Insets insets = styleInfo.calcContentInsets();
        double maxw = sizeInfo.width - insets.getLeft()-insets.getRight();
        if(getWidth() <= 10) return;
        int start = 0;
        int end = 0;
        lines.clear();
        String[] words = text.split(" ");
        String line = "";
        for(int i =0; i<words.length; i++) {
            String word = words[i];


            String testLine = line + " " + word;

            //hard coded newlines
            if(word.contains("\n")) {
                String[] splitWord = word.split("\n");
                testLine = line + " " + splitWord[0];
                lines.add(testLine);
                if(splitWord.length>1) {
                line = splitWord[1];
                testLine = splitWord[1];
                } else {
                    line = "";
                    testLine = "";
                }
            }

            double w = Font.DEFAULT.calculateWidth(testLine);
            if(w > maxw) {
                //if last line
                if(i == words.length-1) {
                    lines.add(testLine);
                } else {
                    lines.add(line);
                    line = word;
                }
                continue;
            }

            //last word
            if(i == words.length-1) {
                lines.add(testLine);
            }

            line = testLine;
        }
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(sizeInfo == null) {
            doPrefLayout();
        }
        boxPainter.draw(g, styleInfo, sizeInfo, this, "");
        g.setPaint(boxPainter.color);
        double y = Font.DEFAULT.calculateHeight("ASDF");
        Insets insets = styleInfo.calcContentInsets();
        y+=insets.getTop();
        double x = insets.getLeft();
        for(String line : lines) {
            g.drawText(line,Font.DEFAULT,x,y);
            y+= Font.DEFAULT.calculateHeight(line);
        }
        //g.setPaint(FlatColor.RED);
        //g.drawRect(0,0,getWidth(),getHeight());
    }

    public Label setText(String text) {
        this.text = text;
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public String getText() {
        return this.text;
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
