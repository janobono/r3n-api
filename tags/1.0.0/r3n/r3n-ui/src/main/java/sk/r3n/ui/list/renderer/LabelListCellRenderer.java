package sk.r3n.ui.list.renderer;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

public abstract class LabelListCellRenderer<T> extends JLabel implements
		ListCellRenderer<T>, Serializable {

	private static final long serialVersionUID = 5566250485986166084L;

	private static Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1,
			1, 1, 1);

	protected String prefix;

	protected String suffix;

	protected boolean readOnly;

	public LabelListCellRenderer() {
		super();
		setOpaque(true);
		prefix = null;
		suffix = null;
		readOnly = false;
	}

	public Component getListCellRendererComponent(JList<? extends T> list,
			T value, int index, boolean isSelected, boolean cellHasFocus) {
		setComponentOrientation(list.getComponentOrientation());
		if (!readOnly && isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		if (cellHasFocus)
			setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
		else
			setBorder(NO_FOCUS_BORDER);
		if (value == null)
			setText(getNullText());
		else {
			String text = getText(value);
			if (prefix != null)
				text = prefix + text;
			if (suffix != null)
				text = text + suffix;
			setText(text);
		}
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

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNullText() {
		return "";
	}

	public abstract String getText(T value);

}
