package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.TableView;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        EventBus.getSystem().addListener(table, MouseEvent.MousePressed, new TreeTableEventHandler());
        return new ScrollPane(table);
    }

    private Control standardTable() {
        TableView table = new TableView();
        table.setWidth(500);
        return new ScrollPane(table);
    }

    private class TreeTableModel implements TableView.TableModel {
        private TreeNode root;
        private HashSet<TreeNode> collapsedSet;

        private TreeTableModel() {
            TreeNode r = new TreeNode("root");
            r.add(new TreeNode("child 1"));
            r.add(new TreeNode("child 2"));
            TreeNode t3 = new TreeNode("child 3 with children");
            t3.add(new TreeNode("sub child 1"));
            t3.add(new TreeNode("sub child 2"));
            r.add(t3);
            r.add(new TreeNode("child 4"));
            r.add(new TreeNode("child 5"));
            r.add(new TreeNode("child 6"));
            r.add(new TreeNode("child 7"));
            this.root = r;
            collapsedSet = new HashSet<TreeNode>();
            collapsedSet.add(t3);
        }

        @Override
        public int getRowCount() {
            return countBreadth();
        }

        private int countBreadth() {
            return countBreadth(root);
        }

        private int countBreadth(TreeNode root) {
            int count = 1;
            if(!collapsedSet.contains(root)) {
                for(TreeNode n : root.children) {
                    count += countBreadth(n);
                }
            }
            return count;
        }
        
        private int getDepth(TreeNode root, Count row) {
            if(row.value == 0) return 1;
            if(!collapsedSet.contains(root)) {
                for(TreeNode n : root.children) {
                    row.value--;
                    int d = getDepth(n,row);
                    if(d >= 0) return d+1;
                }
            }
            return -1;
        }

        private TreeNode findNodeAtRow(TreeNode root, Count row) {
            if(row.value == 0) return root;
            if(!collapsedSet.contains(root)) {
                for(TreeNode n : root.children) {
                    row.value--;
                    TreeNode result = findNodeAtRow(n, row);
                    if(result != null) return result;
                }
            } else {
                //u.p("skipping for: " + root.getTitle());
            }
            return null;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getColumnHeader(int column) {
            switch(column) {
                case 0: return "nav";
                case 1: return "name";
            }
            return "ack!";
        }

        @Override
        public Object get(int row, int column) {
            TreeNode node = findNodeAtRow(root, new Count(row));
            if(node == null) return null;
            switch (column) {
                case 0:
                    if(collapsedSet.contains(node)) return "- " + node.getTitle();
                    if(node.children.isEmpty()) return " " + node.getTitle();
                    return "+ " + node.getTitle();
                case 1: return node.getTitle();
            }
            return "ack ack!";
        }

        public void toggleRow(int row) {
            TreeNode node = findNodeAtRow(root, new Count(row));
            if(node == null) return;
            if(node.children.isEmpty()) return;
            if(collapsedSet.contains(node)) {
                collapsedSet.remove(node);
            } else {
                collapsedSet.add(node);
            }
        }

        public int getDepth(int row) {
            return getDepth(root,new Count(row));
        }

        private class Count {
            private int value;

            public Count(int row) {
                this.value = row;
            }
        }
    }

    private class TreeNode {
        private String title;
        private List<TreeNode> children;

        public TreeNode(String title) {
            this.title = title;
            this.children = new ArrayList<TreeNode>();
        }

        public void add(TreeNode treeNode) {
            this.children.add(treeNode);
        }

        public String getTitle() {
            return title;
        }
    }

    private class TreeTableEventHandler implements Callback<MouseEvent> {
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

    private class TreeTableDataRenderer implements TableView.DataRenderer {
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
}
