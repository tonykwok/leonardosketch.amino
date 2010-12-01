package org.joshy.gfx.test.control.table;

import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Dec 1, 2010
* Time: 2:21:31 AM
* To change this template use File | Settings | File Templates.
*/
public class TreeNode {
    private String first;
    List<TreeNode> children;
    private String last;
    private int age;

    public TreeNode(String first, String last, int age) {
        this.first = first;
        this.last = last;
        this.age = age;
        this.children = new ArrayList<TreeNode>();
    }

    public void add(TreeNode treeNode) {
        this.children.add(treeNode);
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public int getAge() {
        return age;
    }
}
