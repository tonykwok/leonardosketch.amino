package org.joshy.gfx.test.control.table;

import org.joshy.gfx.node.control.TableView;

import java.util.HashSet;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Dec 1, 2010
* Time: 2:21:15 AM
* To change this template use File | Settings | File Templates.
*/
public class TreeTableModel implements TableView.TableModel {
    private TreeNode root;
    private HashSet<TreeNode> collapsedSet;

    public TreeTableModel() {
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
