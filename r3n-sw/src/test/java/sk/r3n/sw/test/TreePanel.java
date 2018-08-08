package sk.r3n.sw.test;

import sk.r3n.sw.component.tree.R3NTree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TreePanel extends JPanel {

    public TreePanel() {
        super(new BorderLayout());

        R3NTree<TestNode> tree = new R3NTree<TestNode>() {
            @Override
            protected TestNode getChild(TestNode parent, int index) {
                return parent.getChildren().get(index);
            }

            @Override
            protected int getChildCount(TestNode parent) {
                return parent.getChildren().size();
            }

            @Override
            protected int getIndexOfChild(TestNode parent, TestNode child) {
                return parent.getChildren().indexOf(child);
            }

            @Override
            protected TestNode getParent(TestNode node) {
                return node.getParent();
            }

            @Override
            protected boolean isLeaf(TestNode node) {
                return node.getChildren().isEmpty();
            }

        };

        TestNode root = new TestNode("root");
        for (int i = 0; i < 10; i++) {
            TestNode branch = new TestNode("b" + i);
            branch.setParent(root);
            root.getChildren().add(branch);
            for (int j = 0; j < 10; j++) {
                TestNode leaf = new TestNode("l" + j);
                leaf.setParent(branch);
                branch.getChildren().add(leaf);
            }
        }
        tree.setRoot(root);

        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private class TestNode {

        private final String name;

        private TestNode parent;

        private List<TestNode> children;

        public TestNode(String name) {
            this.name = name;
        }

        public TestNode getParent() {
            return parent;
        }

        public void setParent(TestNode parent) {
            this.parent = parent;
        }

        public List<TestNode> getChildren() {
            if (children == null) {
                children = new ArrayList<>();
            }
            return children;
        }

        public void setChildren(List<TestNode> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
