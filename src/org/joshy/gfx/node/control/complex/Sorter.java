package org.joshy.gfx.node.control.complex;

import java.util.Comparator;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:31 PM
* To change this template use File | Settings | File Templates.
*/
public interface Sorter<D,H> {


    public Comparator createComparator(TableModel table, int column, TableView.SortOrder order);

}
