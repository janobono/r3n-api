package sk.r3n.sw.component.tree;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class R3NTreeModel<T> implements TreeModel {

    private final EventListenerList eventListenerList;
    protected R3NTree<T> baseTree;

    public R3NTreeModel(R3NTree<T> baseTree) {
        super();
        this.baseTree = baseTree;
        eventListenerList = new EventListenerList();
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        eventListenerList.add(TreeModelListener.class, treeModelListener);
    }

    public void fireTreeStructureChangedEvent() {
        Object[] listeners = eventListenerList.getListenerList();
        TreeModelEvent treeModelEvent = new TreeModelEvent(this, new Object[]{baseTree.root});
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(treeModelEvent);
            }
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == null) {
            return null;
        }
        return baseTree.getChild((T) parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == null) {
            return 0;
        }
        return baseTree.getChildCount((T) parent);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        return baseTree.getIndexOfChild((T) parent, (T) child);
    }

    @Override
    public Object getRoot() {
        return baseTree.root;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node == null) {
            return true;
        }
        return baseTree.isLeaf((T) node);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        eventListenerList.remove(TreeModelListener.class, treeModelListener);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

}
