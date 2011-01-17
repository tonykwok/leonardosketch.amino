package org.joshy.gfx.node.control.complex;

import java.util.HashSet;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:33 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class AbstractTreeTableModel<T,S> implements TreeTableModel<T, S> {
    private T root;
    private HashSet<T> collapsedSet;

    public AbstractTreeTableModel() {
        collapsedSet = new HashSet<T>();
    }

    public void setRoot(T root) {
        this.root = root;
    }

    @Override
    public int getRowCount() {
        return countBreadth();
    }

    private int countBreadth() {
        return countBreadth(root);
    }

    private int countBreadth(T root) {
        int count = 1;
        if(!collapsedSet.contains(root)) {
            for(T n : getChildren(root)) {
                count += countBreadth(n);
            }
        }
        return count;
    }

    public int getDepth(T root, Count row) {
        if(row.value == 0) return 1;
        if(!collapsedSet.contains(root)) {
            for(T n : getChildren(root)) {
                row.value--;
                int d = getDepth(n,row);
                if(d >= 0) return d+1;
            }
        }
        return -1;
    }

    private T findNodeAtRow(T root, Count row) {
        if(row.value == 0) return root;
        if(!collapsedSet.contains(root)) {
            for(T n : getChildren(root)) {
                row.value--;
                T result = findNodeAtRow(n, row);
                if(result != null) return result;
            }
        }
        return null;
    }

    @Override
    public T get(int row, int column) {
        T node = findNodeAtRow(root, new Count(row));
        if(node == null) return null;
        return node;
    }

    public void toggleRow(int row) {
        T node = findNodeAtRow(root, new Count(row));
        if(node == null) return;
        if(!hasChildren(node)) return;
        if(collapsedSet.contains(node)) {
            collapsedSet.remove(node);
        } else {
            collapsedSet.add(node);
        }
    }

    public int getDepth(int row) {
        return getDepth(root,new Count(row));
    }

    public boolean isCollapsed(T cellData) {
        return collapsedSet.contains(cellData);
    }

    public void open(T node) {
        collapsedSet.remove(node);
    }

    public void close(T node) {
        collapsedSet.add(node);
    }

    private class Count {
        private int value;

        public Count(int row) {
            this.value = row;
        }
    }
}
