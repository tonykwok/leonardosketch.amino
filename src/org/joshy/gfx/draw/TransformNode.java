package org.joshy.gfx.draw;

import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.AbstractPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 27, 2010
 * Time: 9:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransformNode extends AbstractPane {
    private Node content;
    private double scaleX;
    private double scaleY;
    private double rotate;

    public void setContent(Node content) {
        this.content = content;
        content.setParent(this);
    }

    public TransformNode setScaleX(double scaleX) {
        this.scaleX = scaleX;
        setDrawingDirty();
        return this;
    }

    public TransformNode setScaleY(double scaleY) {
        this.scaleY = scaleY;
        setDrawingDirty();
        return this;
    }

    public void setRotate(double rotate) {
        this.rotate = rotate;
        setDrawingDirty();
    }

    @Override
    public void draw(GFX g) {
//        g.translate(getTranslateX(),getTranslateY());
        g.scale(scaleX,scaleY);
        g.rotate(rotate,Transform.Z_AXIS);
        content.draw(g);
        g.rotate(-rotate,Transform.Z_AXIS);
        g.scale(1/scaleX,1/scaleY);
//        g.translate(-getTranslateX(),-getTranslateY());
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
    }

    @Override
    public void doPrefLayout() {
        if(content instanceof Control) {
            ((Control)content).doPrefLayout();
        }
    }

    @Override
    public void doLayout() {
        if(content instanceof Control) {
            ((Control)content).doLayout();
        }
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    @Override
    public Iterable<? extends Node> children() {
        List<Node> childs = new ArrayList<Node>();
        childs.add(content);
        return childs;
    }

    @Override
    public Iterable<? extends Node> reverseChildren() {
        List<Node> childs = new ArrayList<Node>();
        childs.add(content);
        return childs;
    }

    public double getRotate() {
        return rotate;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }
}
