package sk.r3n.ui.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public abstract class LabelTableCellRenderer<T> extends JLabel implements
		TableCellRenderer {

	private static final long serialVersionUID = -4767932148932089024L;

	protected static Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(
			1, 1, 1, 1);

	protected String prefix;

	protected String suffix;

	public LabelTableCellRenderer() {
		super();
		setOpaque(true);
		setBorder(NO_FOCUS_BORDER);
		prefix = null;
		suffix = null;
	}

	@SuppressWarnings("unchecked")
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
		if (hasFocus)
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		else
			setBorder(NO_FOCUS_BORDER);
		if (value == null)
			setText(getNullText());
		else {
			String text = getText((T) value);
			if (prefix != null)
				text = prefix + text;
			if (suffix != null)
				text = text + suffix;
			setText(text);
		}

		int height_wanted = (int) getPreferredSize().getHeight();
		if (height_wanted > table.getRowHeight(row))
			table.setRowHeight(row, height_wanted);

		return this;
	}

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

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNullText() {
		return "";
	}

	public abstract String getText(T value);

	protected String getLines(List<String> lines) {
		StringBuffer buffer = new StringBuffer();
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
		preferedSize.setSize(preferedSize.getWidth(), textField
				.getPreferredSize().getHeight() * rowsCount);
		setPreferredSize(preferedSize);
	}
}
