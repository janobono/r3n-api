package sk.r3n.ui.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NCheckBox;
import sk.r3n.ui.util.UIServiceManager;

public class BooleanTableCellRenderer<T> extends R3NCheckBox implements
        TableCellRenderer, UIResource {

    public static class UIResource extends DefaultTableCellRenderer implements
            javax.swing.plaf.UIResource {

    }
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static Border getBorder(boolean cellHasFocus) {
        if (cellHasFocus) {
            return UIManager.getBorder("Table.focusCellHighlightBorder");
        }
        return noFocusBorder;
    }

    public BooleanTableCellRenderer() {
        super();
        setOpaque(true);
        setBorderPaintedFlat(true);
        setBorderPainted(true);
        setBorder(getBorder(false));
        setHorizontalAlignment(CENTER);
        getActionMap().remove(
                UIServiceManager.getDefaultUIService().getActionMapKey(
                UIService.class.getCanonicalName(),
                UIService.ACTION_CELL_OK));
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

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        // Strings get interned...
        if (propertyName.equals("text")) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public void firePropertyChange(String propertyName, short oldValue,
            short newValue) {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setComponentOrientation(table.getComponentOrientation());
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        setEnabled(table.isEnabled());
        setFont(table.getFont());
        setBorder(getBorder(hasFocus));
        if (value == null) {
            setSelected(false);
            setText("");
        } else {
            setSelected(isSelected((T) value));
            setText(getText((T) value));
        }
        int height_wanted = (int) getPreferredSize().getHeight();
        if (height_wanted > table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }

        return this;
    }

    public String getText(T value) {
        return "";
    }

    public boolean isSelected(T value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JList.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    @Override
    public void repaint() {
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    @Override
    public void repaint(Rectangle r) {
    }

    @Override
    public void revalidate() {
    }

    @Override
    public void validate() {
    }
}
