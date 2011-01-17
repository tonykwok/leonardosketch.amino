package org.joshy.gfx.node.control.complex;

import org.joshy.gfx.draw.GFX;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:31 PM
* To change this template use File | Settings | File Templates.
*/
public interface HeaderRenderer<H> {

    public void draw(GFX g, TableView table, H header, int column, double x, double y, double width, double height);

}
