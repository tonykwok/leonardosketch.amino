package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.TableView;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.test.control.table.TreeTableDataRenderer;
import org.joshy.gfx.test.control.table.TreeTableKeyEventHandler;
import org.joshy.gfx.test.control.table.TreeTableMouseEventHandler;
import org.joshy.gfx.test.control.table.TreeTableModel;

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
        TableView table = new TableView();
        table.setWidth(500);
        table.setModel(new TreeTableModel());
        table.setRenderer(new TreeTableDataRenderer());
        EventBus.getSystem().addListener(table, MouseEvent.MousePressed, new TreeTableMouseEventHandler());
        EventBus.getSystem().addListener(table, KeyEvent.KeyPressed, new TreeTableKeyEventHandler());
        return new ScrollPane(table);
    }

    private Control standardTable() {
        TableView table = new TableView();
        table.setWidth(500);
        return new ScrollPane(table);
    }

}
