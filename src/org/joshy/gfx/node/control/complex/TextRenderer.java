package org.joshy.gfx.node.control.complex;

import org.joshy.gfx.node.control.SelectableControl;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 4:18 PM
* To change this template use File | Settings | File Templates.
*/
public interface TextRenderer<E> {
    public String toString(SelectableControl view, E item, int index);
}
