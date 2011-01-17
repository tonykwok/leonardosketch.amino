package org.joshy.gfx.node.control.complex;

import org.joshy.gfx.draw.GFX;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 4:17 PM
* To change this template use File | Settings | File Templates.
*/
public interface ItemRenderer<E> {
    public void draw(GFX gfx, ListView listView, E item, int index, double x, double y, double width, double height);
}
