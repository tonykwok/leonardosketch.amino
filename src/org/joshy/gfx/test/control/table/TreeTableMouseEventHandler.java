package org.joshy.gfx.test.control.table;

import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.TableView;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Dec 1, 2010
* Time: 2:22:07 AM
* To change this template use File | Settings | File Templates.
*/
public class TreeTableMouseEventHandler implements Callback<MouseEvent> {
    @Override
    public void call(MouseEvent event) throws Exception {
        TableView tb = (TableView) event.getSource();
        TreeTableModel model = (TreeTableModel) tb.getModel();
        double rh = tb.getRowHeight();
        int row = (int) (event.getPointInNodeCoords(tb).getY()/rh);
        row--; //take off one to account for the column headers
        model.toggleRow(row);
    }
}
