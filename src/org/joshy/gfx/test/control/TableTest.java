package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.TableView;
import org.joshy.gfx.node.control.TreeView;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.stage.Stage;

public class TableTest implements Runnable {

    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TableTest());
    }

    public void run() {
        Stage s = Stage.createStage();
        s.setContent(new TabPanel()
                .add("table",standardTable())
                .add("tree",tree())
        );
    }

    private Control tree() {
        TreeView treeView = new TreeView();
        treeView.setWidth(500);
        return new ScrollPane(treeView);
    }

    private Control standardTable() {
        TableView table = new TableView();
        table.setWidth(500);
        return new ScrollPane(table);
    }

}
