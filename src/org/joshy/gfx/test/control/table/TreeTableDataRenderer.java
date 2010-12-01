package org.joshy.gfx.test.control.table;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.TableView;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Dec 1, 2010
* Time: 2:22:18 AM
* To change this template use File | Settings | File Templates.
*/
public class TreeTableDataRenderer implements TableView.DataRenderer {
    @Override
    public void draw(GFX g, TableView table, Object cellData, int row, int column, double x, double y, double width, double height) {
        g.setPaint(FlatColor.WHITE);
        if(row == table.getSelectedRow()) {
            g.setPaint(new FlatColor(0xaaaaff));
        }
        g.fillRect(x,y,width,height);
        g.setPaint(FlatColor.BLACK);
        if(cellData != null) {
            TreeTableModel model = (TreeTableModel) table.getModel();
            int depth = model.getDepth(row);
            g.drawText(cellData.toString(), Font.DEFAULT, x+5+10*depth, y+13);
        }
    }
}
