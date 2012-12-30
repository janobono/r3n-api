package sk.r3n.sw.component.field;

public class LongField extends NumericField<Long> {

    public LongField(boolean canBeNull) {
        super(canBeNull);
        setMinValue(Long.MIN_VALUE);
        setMaxValue(Long.MAX_VALUE);
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public Long getValue() {
        if (isContentNull()) {
            return null;
        }
        return Long.parseLong(getText());
    }

    @Override
    public void setValue(Long value) {
        if (value == null) {
            setText("");
        } else {
            setText(Long.toString(value));
        }
    }
}
