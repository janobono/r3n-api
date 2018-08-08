package sk.r3n.sw.component.tree;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public abstract class R3NTreeCellRenderer<T> extends JLabel implements TreeCellRenderer {

    protected boolean hasFocus;

    protected boolean selected;

    public R3NTreeCellRenderer() {
        setHorizontalAlignment(JLabel.LEFT);
    }

    private int getLabelStart() {
        Icon currentI = getIcon();
        if (currentI != null && getText() != null) {
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        }
        return 0;
    }

    @Override
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

        if (value == null) {
            setText(getNullText());
        } else {
            setText(getText((T) value, leaf, root));
        }

        int height_wanted = (int) getPreferredSize().getHeight();
        if (height_wanted > tree.getRowHeight()) {
            tree.setRowHeight(height_wanted);
        }

        return this;
    }

    @Override
    public void paint(Graphics g) {
        Color bColor;
        if (selected) {
            bColor = UIManager.getColor("Tree.selectionBackground");
        } else {
            bColor = UIManager.getColor("Tree.textBackground");
            if (bColor == null) {
                bColor = getBackground();
            }
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

    public String getNullText() {
        return "";
    }

    public abstract String getText(T value, boolean leaf, boolean root);

}
