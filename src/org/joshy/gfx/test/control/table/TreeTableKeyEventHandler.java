package org.joshy.gfx.test.control.table;

import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.node.control.TableView;

public class TreeTableKeyEventHandler implements Callback<KeyEvent> {
    @Override
    public void call(KeyEvent event) throws Exception {
        TableView tb = (TableView) event.getSource();
        TreeTableModel model = (TreeTableModel) tb.getModel();
        int index = tb.getSelectedRow();
        TreeNode node = model.get(index, 0);
        //check for arrow up and down
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
            if(model.hasChildren(node) && model.isCollapsed(node)) {
                model.open(node);
                tb.redraw();
            }
        }
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) {
            if(model.hasChildren(node) && !model.isCollapsed(node)) {
                model.close(node);
                tb.redraw();
            }
        }
    }
}
