package sk.r3n.sw.component.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public abstract class R3NTableCellRenderer<T> extends JLabel implements TableCellRenderer {

    protected static Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    public R3NTableCellRenderer() {
        super();
        setOpaque(true);
        setBorder(NO_FOCUS_BORDER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
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
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(NO_FOCUS_BORDER);
        }
        if (value == null) {
            setText(getNullText());
        } else {
            setText(getText((T) value));
        }
        int height_wanted = (int) getPreferredSize().getHeight();
        if (height_wanted > table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        return this;
    }

    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    public String getNullText() {
        return "";
    }

    public abstract String getText(T value);

    protected String getLines(List<String> lines) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html>");
        for (String line : lines) {
            buffer.append(line);
            buffer.append("<br/>");
        }
        buffer.append("</html>");
        setWantedHeight(lines.size());
        return buffer.toString();
    }

    protected void setWantedHeight(int rowsCount) {
        JTextField textField = new JTextField();
        textField.setFont(getFont());
        textField.setColumns(10);
        Dimension preferedSize = getPreferredSize();
        preferedSize.setSize(preferedSize.getWidth(), textField.getPreferredSize().getHeight() * rowsCount);
        setPreferredSize(preferedSize);
    }

}
