package sk.r3n.ui.component.field;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.border.Border;

public class ValueField<T> extends JTextField {

	private static final long serialVersionUID = -7872102631735018774L;

	protected T value;

	public ValueField() {
		this(null, null);
	}

	public ValueField(int horizontalAlignment) {
		this(null, null, horizontalAlignment);
	}

	public ValueField(Font font, Border border) {
		this(font, border, LEFT);
	}

	public ValueField(Font font, Border border, int horizontalAlignment) {
		super();
		if (font != null)
			setFont(font);
		if (border != null)
			setBorder(border);
		setHorizontalAlignment(horizontalAlignment);
		setFocusable(false);
		setEditable(false);
	}

	public T getValue() {
		return value;
	}

	public void setColumns(int columns) {
		super.setColumns(columns);
		setMinimumSize(getPreferredSize());
	}

	public void setValue(T value) {
		this.value = value;
		if (value == null) {
			setText("");
			return;
		}
		setText(value.toString());
	}

}
