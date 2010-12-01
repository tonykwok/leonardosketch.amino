package org.joshy.gfx.test.control.table;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.GradientFill;
import org.joshy.gfx.node.control.TableView;
import org.joshy.gfx.util.GraphicsUtil;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Dec 1, 2010
* Time: 2:22:18 AM
* To change this template use File | Settings | File Templates.
*/
public class TreeTableDataRenderer implements TableView.DataRenderer<TreeNode> {
    @Override
    public void draw(GFX g, TableView table, TreeNode cellData, int row, int column, double x, double y, double width, double height) {
        //draw the background
        g.setPaint(FlatColor.WHITE);
        if(row == table.getSelectedRow()) {
            //g.setPaint(new FlatColor(0xaaaaff));
            GradientFill grad = new GradientFill(new FlatColor(0x44bcff),new FlatColor(0x0186ba),
                    0,true,0,0,0,height);
            g.setPaint(grad);
        }
        g.translate(x,y);
        g.fillRect(0,0,width,height);
        g.translate(-x,-y);
        g.setPaint(new FlatColor(0xc0c0c0));
        g.drawLine(x,y,x,y+height);

        if(cellData != null) {
            //draw the first column with nav arrows and indentation
            if(column == 0) {
                TreeTableModel model = (TreeTableModel) table.getModel();
                int depth = model.getDepth(row);
                if(model.hasChildren(cellData)) {
                    g.setPaint(new FlatColor(0x606060));
                    if(row == table.getSelectedRow()) {
                        g.setPaint(FlatColor.WHITE);
                    }
                    if(model.isCollapsed(cellData)) {
                        GraphicsUtil.fillRightArrow(g,x+10*depth,y+4,10);
                    } else {
                        GraphicsUtil.fillDownArrow(g,x+10*depth,y+4,10);
                    }
                }
                g.setPaint(FlatColor.BLACK);
                if(row == table.getSelectedRow()) {
                    g.setPaint(FlatColor.WHITE);
                }
                g.drawText(cellData.getFirst(), Font.DEFAULT,x+10*depth+12+5, y+13);
                return;
            }

            //draw the rest of the columns
            String s = "";
            if(column == 1) {
                s = cellData.getLast();
            }
            if(column == 2) {
                s = ""+cellData.getAge();
            }
            g.setPaint(FlatColor.BLACK);
            if(row == table.getSelectedRow()) {
                g.setPaint(FlatColor.WHITE);
            }
            g.drawText(s, Font.DEFAULT, x+5, y+13);
        }
    }
}
