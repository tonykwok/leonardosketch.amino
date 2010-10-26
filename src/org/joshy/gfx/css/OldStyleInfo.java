package org.joshy.gfx.css;

import org.joshy.gfx.node.Insets;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Oct 25, 2010
* Time: 5:15:11 PM
* To change this template use File | Settings | File Templates.
*/
public class OldStyleInfo {
    public double width;
    public double height;
    public double contentWidth;
    public double contentHeight;
    public Insets margin;
    public Insets borderWidth;
    public Insets padding;
    public double contentBaseline;

    public OldStyleInfo() {
    }

    public OldStyleInfo(double width, double height) {
        this.width = width;
        this.height = height;
        this.contentWidth = width;
        this.contentHeight = height;
    }
}
