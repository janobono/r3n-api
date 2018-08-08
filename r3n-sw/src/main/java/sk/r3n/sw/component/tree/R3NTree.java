package sk.r3n.sw.component.tree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class R3NTree<T> extends JTree {

    protected T root;

    public R3NTree() {
        super();
        setModel(new R3NTreeModel<>(this));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void expandNode(T node) {
        node = searchNode(root, node);
        if (node != null) {
            if (isLeaf(node)) {
                node = getParent(node);
            }
            TreePath path = getNodePath(node);
            if (path != null) {
                expandPath(path);
            }
        }
    }

    public void expandNodes(List<T> nodes) {
        for (T node : nodes) {
            expandNode(node);
        }
    }

    protected abstract T getChild(T parent, int index);

    protected abstract int getChildCount(T parent);

    protected abstract int getIndexOfChild(T parent, T child);

    protected abstract T getParent(T node);

    public T getRoot() {
        return root;
    }

    public void setRoot(T root) {
        this.root = root;
        ((R3NTreeModel<?>) getModel()).fireTreeStructureChangedEvent();
    }

    @SuppressWarnings("unchecked")
    public T getSelectedNode() {
        TreePath treePath = getSelectionPath();
        if (treePath != null) {
            if (!treePath.getLastPathComponent().equals(getModel().getRoot())) {
                return (T) treePath.getLastPathComponent();
            }
        }
        return null;
    }

    public void setSelectedNode(T node) {
        if (node != null) {
            TreePath treePath = getNodePath(node);
            if (treePath != null) {
                setSelectionPath(treePath);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getSelectedNodes() {
        List<T> list = new ArrayList<>();
        TreePath[] treePaths = getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                if (treePath != null) {
                    if (!treePath.getLastPathComponent().equals(getModel().getRoot())) {
                        list.add((T) treePath.getLastPathComponent());
                    }
                }
            }
        }
        return list;
    }

    protected abstract boolean isLeaf(T node);

    private T searchNode(T node, T searched) {
        T result = null;
        if (node.equals(searched)) {
            return node;
        } else {
            if (getChildCount(node) > 0) {
                for (int i = 0; i < getChildCount(node); i++) {
                    result = searchNode(getChild(node, i), searched);
                    if (result != null) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private TreePath getNodePath(T node) {
        T searchedNode;
        do {
            searchedNode = searchNode(root, node);
            if (searchedNode == null) {
                node = getParent(node);
            }
            if (node == null) {
                return null;
            }
        } while (searchedNode == null);
        List<T> list = new ArrayList<>();
        searchedNode = node;
        list.add(searchedNode);
        do {
            searchedNode = getParent(searchedNode);
            if (searchedNode != null) {
                list.add(searchedNode);
            }
        } while (searchedNode != null);
        Collections.reverse(list);
        return new TreePath(list.toArray());
    }

}
