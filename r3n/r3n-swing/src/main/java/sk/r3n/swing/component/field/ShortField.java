package sk.r3n.swing.component.field;

public class ShortField extends NumericField<Short> {

    public ShortField(boolean canBeNull) {
        super(canBeNull);
        setMinValue(Short.MIN_VALUE);
        setMaxValue(Short.MAX_VALUE);
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public Short getValue() {
        if (isContentNull()) {
            return null;
        }
        return Short.parseShort(getText());
    }

    @Override
    public void setValue(Short value) {
        if (value == null) {
            setText("");
        } else {
            setText(Short.toString(value));
        }
    }

}
