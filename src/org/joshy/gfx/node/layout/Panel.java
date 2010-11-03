package org.joshy.gfx.node.layout;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.OldStyleInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.node.Node;

public class Panel extends Container {
    protected Insets insets;
    protected FlatColor fill = null;
    protected FlatColor borderColor = FlatColor.BLACK;
    private Callback<Panel> callback;
    protected OldStyleInfo size;

    public Panel() {
        setSkinDirty();
    }

    public Panel onDoLayout(Callback<Panel> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        super.doSkins();
    }
    
    @Override
    public void doPrefLayout() {
        insets = cssSkin.getInsets(this);
        size = cssSkin.getSize(this);
        super.doPrefLayout();
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            size.width = prefWidth;
        } else {
            setWidth(size.width);
        }
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            size.height = prefHeight;
        } else {
            setHeight(size.height);
        }

    }

    @Override
    public void doLayout() {
        if(insets == null) doPrefLayout();
        if(callback != null) {
            try {
                callback.call(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.doLayout();
        }
    }


    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        g.setOpacity(getOpacity());
        drawSelf(g);
        for(Node child : children) {
            g.translate(child.getTranslateX(),child.getTranslateY());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY());
        }
        this.drawingDirty = false;
        g.setOpacity(1.0);
    }

    protected void drawSelf(GFX g) {
        if(fill != null) {
            g.setPaint(fill);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setPaint(borderColor);
            g.drawRect(0,0,getWidth(),getHeight());
            return;
        }
        

        Bounds bounds = new Bounds(0,0,getWidth(),getHeight());
        CSSMatcher matcher = new CSSMatcher(this);
        cssSkin.drawBackground(g,matcher,"",bounds);
        cssSkin.drawBorder(g,matcher,"",bounds);
        return;

    }

    public Panel setFill(FlatColor fill) {
        this.fill = fill;
        return this;
    }

    public Panel add(Node ... nodes) {
        for(Node node : nodes) {
            this.add(node);
        }
        return this;
    }

}
