package sk.r3n.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NTree<T> extends JTree {

	private static final long serialVersionUID = 315292030998818614L;

	protected T root;

	public R3NTree() {
		super();
		setModel(new R3NTreeModel<T>(this));
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Modifikacia klavesovych skratiek
		InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
		KeyStroke[] ks = im.allKeys();
		InputMap im2 = new InputMap();
		if (ks != null) {
			for (int x = 0; x < ks.length; x++) {
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed DOWN"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_DOWN, im2, im.get(ks[x]));
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_NEXT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed LEFT"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_LEFT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed RIGHT"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_RIGHT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed UP"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_UP, im2, im.get(ks[x]));
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_PREVIOUS, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed HOME"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_FIRST, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed END"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_LAST, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed F2"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_CELL_EDIT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed ENTER"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_CELL_OK, im2, im.get(ks[x]));
				}
			}
			setInputMap(JComponent.WHEN_FOCUSED, im2);
		}
		im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ks = im.allKeys();
		im2 = new InputMap();
		if (ks != null) {
			for (int x = 0; x < ks.length; x++) {
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed ESCAPE"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_CELL_CANCEL, im2, im.get(ks[x]));
				}
			}
			setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im2);
		}
		// Modifikácia fokusov
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

	public void expandNode(T node) {
		node = searchNode(root, node);
		if (node != null) {
			if (isLeaf(node))
				node = getParent(node);
			TreePath path = getNodePath(node);
			if (path != null)
				expandPath(path);
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

	@SuppressWarnings("unchecked")
	public List<T> getSelectedNodes() {
		List<T> list = new ArrayList<T>();
		TreePath[] treePaths = getSelectionPaths();
		if (treePaths != null) {
			for (int i = 0; i < treePaths.length; i++) {
				TreePath treePath = treePaths[i];
				if (treePath != null) {
					if (!treePath.getLastPathComponent().equals(
							getModel().getRoot()))
						list.add((T) treePath.getLastPathComponent());
				}
			}
		}
		return list;
	}

	protected abstract boolean isLeaf(T node);

	private T searchNode(T node, T searched) {
		T result = null;
		if (node.equals(searched))
			return node;
		else {
			if (getChildCount(node) > 0) {
				for (int i = 0; i < getChildCount(node); i++) {
					result = searchNode(getChild(node, i), searched);
					if (result != null)
						break;
				}
			}
		}
		return result;
	}

	public void setRoot(T root) {
		this.root = root;
		((R3NTreeModel<?>) getModel()).fireTreeStructureChangedEvent();
	}

	public void setSelectedNode(T node) {
		if (node != null) {
			TreePath treePath = getNodePath(node);
			if (treePath != null) {
				setSelectionPath(treePath);
			}
		}
	}

	private TreePath getNodePath(T node) {
		T searchedNode = null;
		do {
			searchedNode = searchNode(root, node);
			if (searchedNode == null)
				node = getParent(node);
			if (node == null)
				return null;
		} while (searchedNode == null);
		List<T> list = new ArrayList<T>();
		searchedNode = node;
		list.add(searchedNode);
		do {
			searchedNode = getParent(searchedNode);
			if (searchedNode != null)
				list.add(searchedNode);
		} while (searchedNode != null);
		Collections.reverse(list);
		return new TreePath(list.toArray());
	}

}
