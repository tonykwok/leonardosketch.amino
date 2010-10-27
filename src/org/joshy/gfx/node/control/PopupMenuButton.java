package org.joshy.gfx.node.control;

import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;

public class PopupMenuButton<E> extends Button implements SelectableControl {
    private ListModel model;
    private int selectedIndex;
    private PopupMenu popup;
    private ListView.TextRenderer<E> textRenderer;

    public PopupMenuButton()  {
        setSkinDirty();
        setModel(new ListModel<E>() {
            public E get(int i) {
                return (E) ("Dummy item " + i);
            }
            public int size() {
                return 3;
            }
        });
        setSelectedIndex(0);
    }

    public PopupMenuButton<E> setModel(ListModel<E> model) {
        this.model = model;
        return this;
    }

    public PopupMenuButton<E> setTextRenderer(ListView.TextRenderer<E> textRenderer) {
        this.textRenderer = textRenderer;
        return this;
    }

    @Override
    protected void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if(pressed) {
            if(popup == null) {
                popup = new PopupMenu(getModel(), new Callback<ChangedEvent>() {
                    public void call(ChangedEvent event) {
                        setSelectedIndex((Integer)event.getValue());
                    }
                });
                popup.setTextRenderer(this.textRenderer);
                popup.setWidth(200);
                popup.setHeight(200);
                popup.setVisible(false);
                Stage stage = getParent().getStage();
                stage.getPopupLayer().add(popup);
            }
            Point2D pt = NodeUtils.convertToScene(this,0,0-this.getSelectedIndex()*25-PopupMenu.spacer);
            popup.setTranslateX(Math.max(pt.getX(),0));
            popup.setTranslateY(Math.max(pt.getY(),0));
            popup.setVisible(true);
        } else {
            popup.setVisible(false);
        }
    }


    @Override
    public void doPrefLayout() {
        super.doPrefLayout();
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width+20);
        }
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;

        CSSMatcher matcher = new CSSMatcher("PopupMenuButton");
        Bounds bounds = new Bounds(0,0,getWidth(),getHeight());
        cssSkin.drawBackground(g,matcher,"", bounds);
        int col = cssSkin.getCSSSet().findColorValue(matcher, "color");
        g.setPaint(new FlatColor(col));
        drawText(g);
        drawTriangle(g);
        cssSkin.drawBorder(g,matcher,"",bounds);
    }

    private void drawTriangle(GFX g) {
        double[] points = new double[]{0,0, 14,0, 7, 9};
        g.translate(getWidth()-22,10);
        g.fillPolygon(points);
        g.translate(-getWidth()+22,-10);
    }

    private void drawText(GFX g) {
        E o = getSelectedItem();
        String s = o.toString();
        if(textRenderer != null) {
            s = textRenderer.toString(this,o,0);
        }
        Font.drawCenteredVertically(g, s, cssSkin.getDefaultFont(),6, 0, getWidth(), getHeight(), true);
    }

    public E getSelectedItem() {
        return getModel().get(getSelectedIndex());
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        this.text = getSelectedItem().toString();
        EventBus.getSystem().publish(new SelectionEvent(SelectionEvent.Changed,this));
        setDrawingDirty();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public ListModel<E> getModel() {
        return model;
    }

}
