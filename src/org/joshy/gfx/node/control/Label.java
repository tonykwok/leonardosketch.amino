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

public class Label extends Control {
    private String text = "Label";
    private StyleInfo styleInfo;
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private Font realFont;
    public TextLayoutModel _layout_model;

    public Label(String text) {
        this.text = text;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this,realFont);
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
    }

    @Override
    public void doLayout() {
        layoutText();
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        boxPainter = cssSkin.createBoxPainter(this,styleInfo,sizeInfo,text,CSSSkin.State.None);
    }

    private void layoutText() {
        _layout_model = new TextLayoutModel(styleInfo.font,getText(), true);
        _layout_model.layout(getWidth(),getHeight());
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(sizeInfo == null) {
            doPrefLayout();
        }
        boxPainter.draw(g, styleInfo, sizeInfo, this, "");
        g.setPaint(boxPainter.color);
        double y = styleInfo.font.getAscender();
        Insets insets = styleInfo.calcContentInsets();
        y+=insets.getTop();
        double x = insets.getLeft();
        for(TextLayoutModel.LayoutLine line : _layout_model.lines()) {
            g.drawText(line.getString(),styleInfo.font,x,y);
            y+= line.getHeight();
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

    public Label setFont(Font font) {
        this.realFont = font;
        setSkinDirty();
        return this;
    }
}
