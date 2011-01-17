package org.joshy.gfx.node.control.complex;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:31 PM
* To change this template use File | Settings | File Templates.
*/
public interface TableModel<D,H> {

    public int getRowCount();

    public int getColumnCount();

    public H getColumnHeader(int column);

    /**
     * Return the data item at the specified row and createColumn. May return null.
     * @param row
     * @param column
     * @return the data at the specified row and createColumn. May be null.
     */
    public D get(int row, int column);

}
