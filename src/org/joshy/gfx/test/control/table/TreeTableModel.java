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
public class TreeTableModel implements TableView.TableModel<TreeNode, String> {
    private TreeNode root;
    private HashSet<TreeNode> collapsedSet;

    public TreeTableModel() {
        TreeNode r = new TreeNode("Master","Control Program",10000);
        r.add(new TreeNode("Bart","Simpson",10));
        r.add(new TreeNode("Lisa","Simpson",8));
        TreeNode t3 = new TreeNode("Maggy","Simpson",1);
        t3.add(new TreeNode("Snowball","Cat",5));
        t3.add(new TreeNode("Santa's","Lil' helper",5));
        r.add(t3);
        r.add(new TreeNode("Mr.","Burns",100));
        r.add(new TreeNode("Waylon","Smithers",45));
        r.add(new TreeNode("Principle","Skinner",55));
        r.add(new TreeNode("Jebadiah","Springfield",250));
        this.root = r;
        collapsedSet = new HashSet<TreeNode>();
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
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnHeader(int column) {
        switch(column) {
            case 0: return "first";
            case 1: return "last";
            case 2: return "age";
        }
        return "ack!";
    }

    @Override
    public TreeNode get(int row, int column) {
        TreeNode node = findNodeAtRow(root, new Count(row));
        if(node == null) return null;
        return node;
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

    public boolean hasChildren(TreeNode treeNode) {
        if(treeNode.children.size()>0) {
            return true;
        }
        return false;
    }

    public boolean isCollapsed(TreeNode cellData) {
        return collapsedSet.contains(cellData);
    }

    public void open(TreeNode node) {
        collapsedSet.remove(node);
    }

    public void close(TreeNode node) {
        collapsedSet.add(node);
    }

    private class Count {
        private int value;

        public Count(int row) {
            this.value = row;
        }
    }
}
