package sk.r3n.ui.component.field;

public class IntegerField extends NumericField<Integer> {

	private static final long serialVersionUID = -1593124602512951076L;

	public IntegerField(boolean canBeNull) {
		super(canBeNull);
		setMinValue(Integer.MIN_VALUE);
		setMaxValue(Integer.MAX_VALUE);
		setHorizontalAlignment(RIGHT);
	}

	public Integer getValue() {
		if (isContentNull())
			return null;
		return Integer.parseInt(getText());
	}

	public void setValue(Integer value) {
		if (value == null)
			setText("");
		else {
			setText(Integer.toString(value));
		}
	}

}
