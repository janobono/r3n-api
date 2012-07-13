package sk.r3n.ui.component.field;

public class ShortField extends NumericField<Short> {

	private static final long serialVersionUID = 2901683703990762570L;

	public ShortField(boolean canBeNull) {
		super(canBeNull);
		setMinValue(Short.MIN_VALUE);
		setMaxValue(Short.MAX_VALUE);
		setHorizontalAlignment(RIGHT);
	}

	public Short getValue() {
		if (isContentNull())
			return null;
		return Short.parseShort(getText());
	}

	public void setValue(Short value) {
		if (value == null)
			setText("");
		else {
			setText(Short.toString(value));
		}
	}

}
