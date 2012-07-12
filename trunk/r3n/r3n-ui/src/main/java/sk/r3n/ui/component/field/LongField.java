package sk.r3n.ui.component.field;

public class LongField extends NumericField<Long> {

	private static final long serialVersionUID = -1340518693935321648L;

	public LongField(boolean canBeNull) {
		super(canBeNull);
		setMinValue(Long.MIN_VALUE);
		setMaxValue(Long.MAX_VALUE);
		setHorizontalAlignment(RIGHT);
	}

	public Long getValue() {
		if (isContentNull())
			return null;
		return Long.parseLong(getText());
	}

	public void setValue(Long value) {
		if (value == null)
			setText("");
		else {
			setText(Long.toString(value));
		}
	}

}
