package org.joshy.gfx.node.control.complex;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:31 PM
* To change this template use File | Settings | File Templates.
*/
public interface Filter<D,H> {

    public boolean matches(TableModel<D, H> table, int row);

}
