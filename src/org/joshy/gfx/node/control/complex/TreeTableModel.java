package org.joshy.gfx.node.control.complex;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:32 PM
* To change this template use File | Settings | File Templates.
*/
public interface TreeTableModel<T, S> extends TableModel {
    public int getColumnCount();
    public int getRowCount();
    public boolean hasChildren(T node);
    public Iterable<T> getChildren(T node);
    public boolean isCollapsed(T node);
    public S getColumnHeader(int column);
    public void toggleRow(int row);
    public T get(int row, int column);
    public void open(T node);
    public void close(T node);
    public int getDepth(int row);
    public S getColumnData(T node, int column);
}
