package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.util.u;

public class Button extends Control {
    protected String text;
    protected boolean pressed = false;
    protected boolean hovered = false;
    protected boolean selected;
    protected boolean selectable = false;
    protected String style;
    private Callback<ActionEvent> callback;
    protected BoxPainter boxPainter;

    public boolean isSelected() {
        return selected;
    }


    public static enum State {
        Pressed("pressed"),
        Normal("normal"),
        Selected("selected"), SelectedPressed("selected-pressed"), Hovered("hovered");

        private String key;

        State(String s) {
            this.key = s;
        }

        @Override
        public String toString() {
            return this.key;
        }
    };


    public Button() {
        text = "Button text";
        setSkinDirty();
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                if(event.getType() == MouseEvent.MousePressed) {
                    setPressed(true);
                    if(selectable) {
                        selected = !selected;
                    }
                    setSkinDirty();
                    setDrawingDirty();
                }
                if(event.getType() == MouseEvent.MouseReleased) {
                    setPressed(false);
                    setSkinDirty();
                    setDrawingDirty();
                    fireAction();
                }
                if(event.getType() == MouseEvent.MouseEntered) {
                    setHovered(true);
                    setSkinDirty();
                    setDrawingDirty();
                }
                if(event.getType() == MouseEvent.MouseExited) {
                    setHovered(false);
                    setSkinDirty();
                    setDrawingDirty();
                }
            }
        });
    }
    
    public Button onClicked(Callback<ActionEvent> callback) {
        this.callback = callback;
        return this;
    }

    private void fireAction() {
        if(!isEnabled()) return;
        ActionEvent action = new ActionEvent(ActionEvent.Action, Button.this);
        EventBus.getSystem().publish(action);
        if(callback != null) {
            try {
                callback.call(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    protected void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public Button(String text) {
        this();
        this.text = text;        
    }

    protected StyleInfo styleInfo;
    protected SizeInfo sizeInfo;

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
    }

    @Override
    public void doLayout() {
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        State state = calculateState();
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, text, buttonStateToCssState(state));
    }
    
    private CSSSkin.State buttonStateToCssState(State state) {
        if(!isEnabled()) {
            return CSSSkin.State.Disabled;
        }
        switch(state) {
            case Selected: return CSSSkin.State.Selected;
            case SelectedPressed: return CSSSkin.State.Selected;
            case Normal: return CSSSkin.State.None;
            case Pressed: return CSSSkin.State.Pressed;
            case Hovered: return CSSSkin.State.Hover;
        }
        return CSSSkin.State.None;
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        g.setPaint(new FlatColor(1,0,0,1));

        if(sizeInfo == null) {
            doPrefLayout();
        }
        boxPainter.draw(g, styleInfo, sizeInfo, this, text);
    }

    private State calculateState() {
        State state = State.Normal;
        if(hovered && !pressed) state = State.Hovered;
        if(selected && !pressed) state = State.Selected;
        if(!selected && pressed) state = State.Pressed;
        if(selected && pressed) state = State.SelectedPressed;
        return state;        
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(getTranslateX(),getTranslateY(),getWidth(),getHeight());
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    @Override
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public double getBaseline() {
        if(styleInfo == null) {
            doPrefLayout();
        }
        return styleInfo.margin.getTop()
                + styleInfo.borderWidth.getTop()
                + styleInfo.padding.getTop()
                + styleInfo.contentBaseline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setLayoutDirty();
        setDrawingDirty();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setDrawingDirty();
    }

}
