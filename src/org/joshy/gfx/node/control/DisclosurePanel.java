package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.Container;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 27, 2010
 * Time: 3:05:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DisclosurePanel extends Container {
    private Control title;
    private Control content;
    private Button button;
    private boolean open;

    public DisclosurePanel() {
        this.button = new Button("+");
        add(button);

        EventBus.getSystem().addListener(button, ActionEvent.Action, new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                setOpen(!isOpen());
            }
        });
    }

    public DisclosurePanel setTitle(Control title) {
        this.title = title;
        add(title);
        return this;
    }

    public DisclosurePanel setContent(Control content) {
        this.content = content;
        add(content);
        return this;
    }

    @Override
    public void doPrefLayout() {
        super.doPrefLayout();
        double w1 = button.getLayoutBounds().getWidth()+title.getLayoutBounds().getWidth();
        double w2 = 0;
        double h1 = button.getLayoutBounds().getHeight();
        if(isOpen()) {
            w2 = content.getLayoutBounds().getWidth();
            h1+=content.getLayoutBounds().getHeight();
        }
        setWidth(Math.max(w1,w2));
        setHeight(h1);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        button.setTranslateX(0);
        button.setTranslateY(0);
        Bounds b = button.getLayoutBounds();

        title.setTranslateX(b.getX2());
        title.setTranslateY(0);

        double y = b.getY2();
        y = Math.max(y,title.getLayoutBounds().getY2());

        content.setVisible(isOpen());
        content.setTranslateX(0);
        content.setTranslateY(y);
    }

    @Override
    public void draw(GFX g) {
        for(Node child : children) {
            if(!child.isVisible()) continue;
            g.translate(child.getTranslateX(),child.getTranslateY());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY());
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        setLayoutDirty();
        button.setText(isOpen()?"-":"+");
    }
}
