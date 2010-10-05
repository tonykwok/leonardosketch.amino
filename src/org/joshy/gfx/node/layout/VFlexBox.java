package org.joshy.gfx.node.layout;

import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.util.u;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Aug 28, 2010
* Time: 6:51:48 PM
* To change this template use File | Settings | File Templates.
*/
public class VFlexBox extends FlexBox {

    @Override
    public void doPrefLayout() {
        insets = cssSkin.getInsets(this);

        double totalHeight = 0;
        double maxWidth = 0;
        for(Control c : controlChildren()) {
            c.doPrefLayout();
            Bounds bounds = c.getLayoutBounds();
            if(c instanceof SplitPane) {
                //reset to 0
                c.setHeight(0);
            }
            totalHeight += bounds.getHeight();
            maxWidth = Math.max(maxWidth, bounds.getWidth());
        }

        if(getPrefWidth() == CALCULATED) {
            setWidth(maxWidth+insets.getLeft()+insets.getRight());
        } else {
            setWidth(getPrefWidth());
        }
        if(getPrefHeight() == CALCULATED) {
            setHeight(totalHeight+insets.getTop()+insets.getBottom());
        } else {
            setHeight(getPrefHeight());
        }

    }

    @Override
    public void doLayout() {
        if(insets == null) doPrefLayout();
        //set children to their preferred width first
        //and calc total width & flex
        double totalHeight = 0;
        double totalFlex = 0;
        for(Control c : controlChildren()) {
            c.doPrefLayout();
            Bounds bounds = c.getLayoutBounds();
            if(bounds == null) {
                u.p("ERROR! control " + c + " is returning null layout bounds: " + bounds );
                return;
            }
            if(c instanceof SplitPane) {
                //reset to 0
                c.setHeight(0);
                //c.setWidth(0);
            }
            totalHeight += bounds.getHeight();
            //u.p("spacemap = " + spaceMap.get(c));
            if(spaceMap.containsKey(c)) {
                totalFlex += spaceMap.get(c);
            } else {
                totalFlex += 0;
            }
        }

        double totalExcess = getHeight()-totalHeight-insets.getTop()-insets.getBottom();

        double y = 0;
        for(Control c : controlChildren()) {
            Bounds bounds = c.getLayoutBounds();
            //position child first
            c.setTranslateX(0+insets.getLeft());
            c.setTranslateY(y+insets.getTop());
            //set the height
            double flex = 0;
            if(spaceMap.containsKey(c)) {
                flex = spaceMap.get(c);
            } else {
                u.p("WARNING: Control "+ c + " has no flex value");
            }
            if(totalFlex > 0) {
                c.setHeight(c.getHeight()+flex/totalFlex*totalExcess);
            }
            //update running total
            y = y + c.getHeight();
            //set the width
            if(align == Align.Stretch) {
                c.setWidth(getWidth()-insets.getLeft()-insets.getRight());
            }
            if(align == Align.Right) {
                c.setTranslateX(getWidth()-bounds.getWidth());
            }
            c.doLayout();
        }
    }
}
