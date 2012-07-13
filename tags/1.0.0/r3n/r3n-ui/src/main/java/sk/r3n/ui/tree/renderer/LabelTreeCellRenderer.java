package sk.r3n.ui.tree.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

public abstract class LabelTreeCellRenderer<T> extends JLabel implements
		TreeCellRenderer {

	private static final long serialVersionUID = 2958256833861475158L;

	protected boolean hasFocus;

	protected boolean selected;

	public LabelTreeCellRenderer() {
		setHorizontalAlignment(JLabel.LEFT);
	}

	@Override
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, byte oldValue,
			byte newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, double oldValue,
			double newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, float oldValue,
			float newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, char oldValue,
			char newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, long oldValue,
			long newValue) {
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// Strings get interned...
		if (propertyName.equals("text"))
			super.firePropertyChange(propertyName, oldValue, newValue);
	}

	@Override
	public void firePropertyChange(String propertyName, short oldValue,
			short newValue) {
	}

	private int getLabelStart() {
		Icon currentI = getIcon();
		if (currentI != null && getText() != null) {
			return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		boolean root = tree.getModel().getRoot().equals(value);
		this.hasFocus = hasFocus;
		this.selected = selected;
		if (selected) {
			setForeground(UIManager.getColor("Tree.selectionForeground"));
			setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			setBackground(tree.getBackground());
			setForeground(tree.getForeground());
		}
		setComponentOrientation(tree.getComponentOrientation());

		if (value == null)
			setText(getNullText());
		else
			setText(getText((T) value, leaf, root));

		int height_wanted = (int) getPreferredSize().getHeight();
		if (height_wanted > tree.getRowHeight())
			tree.setRowHeight(height_wanted);

		return this;
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void invalidate() {
	}

	/**
	 * Vykresluje hodnotu. Pozadie je vyplnene na zaklade priznaku selected.
	 */
	public void paint(Graphics g) {
		Color bColor;
		if (selected) {
			bColor = UIManager.getColor("Tree.selectionBackground");
		} else {
			bColor = UIManager.getColor("Tree.textBackground");
			if (bColor == null)
				bColor = getBackground();
		}
		int imageOffset = -1;
		if (bColor != null) {
			imageOffset = getLabelStart();
			g.setColor(bColor);
			if (getComponentOrientation().isLeftToRight()) {
				g.fillRect(imageOffset, 0, getWidth() - imageOffset,
						getHeight());
			} else {
				g.fillRect(0, 0, getWidth() - imageOffset, getHeight());
			}
		}

		if (hasFocus) {
			if (imageOffset == -1) {
				imageOffset = getLabelStart();
			}
			if (getComponentOrientation().isLeftToRight()) {
				paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
						getHeight());
			} else {
				paintFocus(g, 0, 0, getWidth() - imageOffset, getHeight());
			}
		}
		super.paint(g);
	}

	private void paintFocus(Graphics g, int x, int y, int w, int h) {
		Color bsColor = UIManager.getColor("Tree.selectionBorderColor");
		if (bsColor != null) {
			g.setColor(bsColor);
			g.drawRect(x, y, w - 1, h - 1);
		}
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void repaint() {
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void repaint(Rectangle r) {
	}

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void revalidate() {
	}

	public String getNullText() {
		return "";
	}

	public abstract String getText(T value, boolean leaf, boolean root);

	/**
	 * Prekryte kvoli vykonu.
	 */
	@Override
	public void validate() {
	}

}
