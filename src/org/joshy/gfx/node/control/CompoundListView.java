package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 the design of list view 2 is to not use a renderer and editor that are reused. instead the
 programmer provides a factory which is called to create a new control instance for each item.
 this instance is a real control that will be added to the scene, and will then draw itself and respond
 to input just like any other control.

 tricky parts:

 do we reuse item views?

 how do we handle coordinate transforms for input since the input calculation is handled in the stage?
 what is the signature of an item view? maybe just control (so it can be resized)

 how does the master list view get key events which may really be going to a child?

 view calls Control createItemView(CompoundListView, int itemIndex);
 it's up to the factory to create a control and give it a reference to the item. it's also
 up to the item to draw itself correctly when it's item is selected.


 * make external factory interface for creating the controls
 //* hook up to proper scroll pane
 * make more efficient by better caching views and only creating the ones that are really necessary
 * figure out how to capture keystroke events that may really go to children

 */
public class CompoundListView extends Control implements Parent, Focusable, ScrollPane.ScrollingAware {
    private ListModel model;
    private List<Control> views;
    private double itemViewHeight = 30;
    private int selectedIndex = -1;
    private double scrollY = 0;
    private boolean focused = false;
    private ItemViewFactory itemViewFactory;


    public void setRowHeight(double height) {
        this.itemViewHeight = height;
        setLayoutDirty();
        setDrawingDirty();
    }
    public CompoundListView() {
        views = new ArrayList<Control>();

        EventBus.getSystem().addListener(Scope.Container, this, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                event.accept();
                //check for focus changes
                if (event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
                    if (event.isShiftPressed()) {
                        Core.getShared().getFocusManager().gotoPrevFocusableNode();
                    } else {
                        Core.getShared().getFocusManager().gotoNextFocusableNode();
                    }
                }
                //check for arrow up and down
                if (event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
                    int index = getSelectedIndex() + 1;
                    if (index < getModel().size()) {
                        setSelectedIndex(index);
                    }
                }
                if (event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
                    int index = getSelectedIndex() - 1;
                    if (index >= 0) {
                        setSelectedIndex(index);
                    }
                }
            }
        });

        EventBus.getSystem().addListener(Scope.Container, this, MouseEvent.MouseAll, new Callback<MouseEvent>() {
            public void call(MouseEvent event) {
                if (event.getType() == MouseEvent.MousePressed) {
                    Point2D pt_scene = NodeUtils.convertToScene((Node) event.getSource(),event.getX(),event.getY());
                    pt_scene = NodeUtils.convertFromScene(CompoundListView.this, pt_scene);
                    int startRow = (int) (-scrollY / itemViewHeight);
                    setSelectedIndex((int) (pt_scene.getY() / itemViewHeight) + startRow);
                    setDrawingDirty();
                    Core.getShared().getFocusManager().setFocusedNode(CompoundListView.this);
                }
            }
        });

        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>() {
            public void call(FocusEvent event) {
                if (event.getType() == FocusEvent.Lost && event.getSource() == CompoundListView.this) {
                    focused = false;
                    setDrawingDirty();
                }
                if (event.getType() == FocusEvent.Gained && event.getSource() == CompoundListView.this) {
                    focused = true;
                    setDrawingDirty();
                }
            }
        });

        setModel(ListView.createModel(new String[]{"1","2","3","4","5"}));
        setItemViewFactory(new ItemViewFactory() {
            @Override
            public Control createItemView(CompoundListView listView, final int index, Control prev) {
                Object item = model.get(index);
                String text = "";
                if (item != null) {
                    text = item.toString();
                }
                final String finalText = text;
                return new Control() {
                    @Override
                    public void doLayout() {
                    }

                    @Override
                    public void doPrefLayout() {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void doSkins() {
                    }

                    @Override
                    public void draw(GFX g) {
                        if (getSelectedIndex() == index) {
                            g.setPaint(FlatColor.BLUE);
                        } else {
                            g.setPaint(FlatColor.WHITE);
                        }
                        g.fillRect(0, 0, getWidth(), itemViewHeight);

                        if (getSelectedIndex() == index) {
                            g.setPaint(FlatColor.WHITE);
                        } else {
                            g.setPaint(FlatColor.BLACK);
                        }
                        Font.drawCenteredVertically(g, finalText, Font.DEFAULT, 5, 0, getWidth(), itemViewHeight, true);
                    }
                };
            }
        });
    }

    public void setItemViewFactory(ItemViewFactory itemViewFactory) {
        this.itemViewFactory = itemViewFactory;
    }

    public void setModel(ListModel model) {
        this.model = model;
        EventBus.getSystem().addListener(model, ListView.ListEvent.Updated, new Callback<ListView.ListEvent>() {
            public void call(ListView.ListEvent event) {
                u.p("compound list view got an update from the model");
                regenerateItemViews();
            }
        });
        
        setSelectedIndex(-1);
        setDrawingDirty();
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < getModel().size()) {
//            u.p("really set index to "+ index + " model size = " + getModel().size());
            this.selectedIndex = index;
            setDrawingDirty();
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }


    private void regenerateItemViews() {
        //nuke all of the old ones
        for (Control c : views) {
            c.setParent(null);
        }
        views.clear();

        //create the new ones
        int startIndex = (int) (-scrollY / itemViewHeight);
        int viewCount = (int) (getHeight() / itemViewHeight);
        //u.p("regen item views. setStart index = " + startIndex + " new view count = " + viewCount);
        for (int i = startIndex; i < startIndex + viewCount + 1; i++) {
            Control itemView = itemViewFactory.createItemView(this, i, null);
            if(itemView != null) {
                views.add(itemView);
                itemView.setParent(this);
            }
        }
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
    }

    public double getFullWidth(double width, double height) {
        return width; //always stretch to fill the available width
    }

    public double getFullHeight(double width, double height) {
        return Math.max(
                getModel().size() * itemViewHeight,
                height);
    }

    public void setScrollX(double value) {
        //we don't use the scrollx property. lists only scroll vertically
    }

    /* ======= scrolling related ============ */
    public void setScrollY(double value) {
        if (value <= 0) {
            this.scrollY = value;
        } else {
            this.scrollY = 0;
        }
        regenerateItemViews();
        setDrawingDirty();
    }

    public void setScrollParent(ScrollPane scrollPane) {
        
    }

    public double getScrollY() {
        return scrollY;
    }


    @Override
    public Control setHeight(double height) {
        double oldHeight = getHeight();
        super.setHeight(height);
        if (oldHeight != height) {
            Core.getShared().defer(new Runnable() {
                public void run() {
                    regenerateItemViews();
                }
            });
        }
        return this;
    }

    @Override
    public void doSkins() {
        for (Node n : children()) {
            if (n instanceof Control) {
                ((Control) n).doSkins();
            }
        }
    }

    @Override
    public void doPrefLayout() {
        for (Node n : children()) {
            if (n instanceof Control) {
                Control c = (Control) n;
                c.doPrefLayout();
            }
        }
    }
    
    @Override
    public void doLayout() {
        double yoff = scrollY % itemViewHeight;
        double y = yoff;
        for (Node n : children()) {
            n.setTranslateY(y);
            if (n instanceof Control) {
                Control c = (Control) n;
                c.setWidth(getWidth());
                c.setHeight(itemViewHeight);
                c.doLayout();
            }
            y += itemViewHeight;
        }
    }

    @Override
    public void draw(GFX g) {
        //set new clip
        Bounds clip = g.getClipRect();
        g.setClipRect(new Bounds(0, 0, width, height));

        //draw background
        g.setPaint(FlatColor.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        /*double yoff = scrollY%itemViewHeight;
        //draw children
        for(int i =0; i<views.size();i++) {
            Control iv = views.get(i);
            g.translate(0,i*itemViewHeight-yoff);
            iv.draw(g);
            g.translate(0,-i*itemViewHeight+yoff);
        }*/
        for (Control c : views) {
            g.translate(c.getTranslateX(), c.getTranslateY());
            c.draw(g);
            g.translate(-c.getTranslateX(), -c.getTranslateY());
        }

        //draw border
        g.setPaint(FlatColor.BLACK);
        if (isFocused()) {
            g.setPaint(FlatColor.BLUE);
        }
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        //restore old clip
        g.setClipRect(clip);
    }

    public void setSkinDirty(Node node) {
        setSkinDirty();
    }

    public void setDrawingDirty(Node node) {
        setDrawingDirty();
    }

    public void setLayoutDirty(Node node) {
        setLayoutDirty();
    }

    public Iterable<? extends Node> children() {
        return views;
    }

    public Iterable<? extends Node> reverseChildren() {
        List<Control> viewsr = new ArrayList<Control>();
        viewsr.addAll(views);
        Collections.reverse(viewsr);
        return viewsr;
    }

    public Stage getStage() {
        return getParent().getStage();
    }

    public ListModel getModel() {
        return model;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setContentsDirty() {
        setDrawingDirty();
        regenerateItemViews();
    }

    public static abstract class ItemViewFactory {
        public abstract Control createItemView(CompoundListView listView, int index, Control prev);
    }
}
