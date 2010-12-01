package org.joshy.gfx.test.control.table;

import org.joshy.gfx.test.control.TableTest;

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
    private String title;
    List<TreeNode> children;

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
