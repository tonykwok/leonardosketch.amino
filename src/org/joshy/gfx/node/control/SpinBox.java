package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.Container;

/**
 * The SpinBox is a small text box with up and down buttons that let the user
 * edit a numeric value using the buttons or the arrow keys.
 *
 */
public class SpinBox<E extends Number> extends Container {
    private Textbox valueBox;
    E value;
    private Callback<ChangedEvent> changedCallback;
    private Label label;
    private Button incrementButton;
    private Button decrementButton;

    public SpinBox() {
        valueBox = new SpinBoxBox();
        valueBox.setText(""+value);
        valueBox.setPrefWidth(50);
        this.label = new Label("");
        incrementButton = new SpinButton("+",true);
        decrementButton = new SpinButton("-",false);

        //this.add(label);
        this.add(valueBox);
        this.add(incrementButton);
        incrementButton.onClicked(new Callback<ActionEvent>(){
                public void call(ActionEvent actionEvent) {
                    increment(1);
                    valueBox.setText(""+value);
                    fireUpdate();
                }
            });
        this.add(decrementButton);
        decrementButton.onClicked(new Callback<ActionEvent>(){
                public void call(ActionEvent actionEvent) {
                    increment(-1);
                    valueBox.setText(""+value);
                    fireUpdate();
                }
            });

        EventBus.getSystem().addListener(valueBox, FocusEvent.Gained, new Callback<FocusEvent>(){
            public void call(FocusEvent focusEvent) {
                valueBox.selectAll();
            }
        });
        EventBus.getSystem().addListener(valueBox, ActionEvent.Action, new Callback<ActionEvent>(){
            public void call(ActionEvent event) {
                int va = Integer.parseInt(valueBox.getText());
                value = (E) new Integer(va);
                fireUpdate();
            }
        });

        setWidth(100);
        setHeight(100);
    }

    public SpinBox(String s) {
        this();
        this.label.setText(s);
    }

    private void increment(int i) {
        if(value instanceof Integer) {
            int v = value.intValue() + i;
            value = (E) new Integer(v);
        }
        if(value instanceof Double) {
            double v = value.doubleValue() +i;
            value = (E) new Double(v);
        }
    }

    public SpinBox<E> setLabel(String text) {
        this.label.setText(text);
        return this;
    }

    private void fireUpdate() {
        if(changedCallback != null) {
            try {
                if(value instanceof Integer) {
                    changedCallback.call(new ChangedEvent(ChangedEvent.IntegerChanged,value,this));
                }
                if(value instanceof Double) {
                    changedCallback.call(new ChangedEvent(ChangedEvent.DoubleChanged,value,this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SpinBox<E> setValue(E val) {
        this.value = val;
        this.valueBox.setText(""+value);
        return this;
    }
    public SpinBox onChanged(Callback<ChangedEvent> callback) {
        this.changedCallback = callback;
        return this;
    }


    @Override
    public void doPrefLayout() {
        setHeight(30);
        setWidth(100);
        super.doPrefLayout();
    }


    @Override
    public void doLayout() {
        double h = 30;
        double l = 40;

        valueBox.setTranslateX(0);
        valueBox.setTranslateY(0);
        valueBox.setWidth(l);
        valueBox.setHeight(h);

        incrementButton.setTranslateX(l);
        decrementButton.setTranslateX(l);
        incrementButton.setTranslateY(0);
        decrementButton.setTranslateY(h/2);
        incrementButton.setWidth(h/2);
        decrementButton.setWidth(h/2);
        incrementButton.setHeight(h/2);
        decrementButton.setHeight(h/2);

        for(Control c : controlChildren()) {
            c.doLayout();
        }
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
//        g.setOpacity(getOpacity());
//        drawSelf(g);
        for(Node child : children) {
            g.translate(child.getTranslateX(),child.getTranslateY());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY());
        }
        this.drawingDirty = false;
//        g.setOpacity(1.0);
    }


    private static class SpinButton extends Button {
        private boolean top;

        public SpinButton(String s, boolean top) {
            super("");
            this.top = top;
            if(top) {
                cssClasses.add("-SpinButton-top");
            } else {
                cssClasses.add("-SpinButton-bottom");
            }
        }
    }

    private static class SpinBoxBox extends Textbox {
        private SpinBoxBox() {
            cssClasses.add("-SpinButton-box");
        }
    }
}
