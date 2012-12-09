package sk.r3n.sw.component.field;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public abstract class NumericField<T> extends R3NField<T> {

    protected T maxValue;

    protected T minValue;

    public static final char MINUS_SIGN = DecimalFormatSymbols.getInstance(Locale.getDefault()).getMinusSign();

    public NumericField(boolean canBeNull) {
        super(canBeNull);
    }

    protected int getMaxCharCount() {
        int minLength = 0;
        if (minValue != null) {
            minLength = minValue.toString().length();
        }
        int maxLength = 0;
        if (maxValue != null) {
            maxLength = maxValue.toString().length();
        }
        if (minLength > maxLength) {
            return minLength;
        }
        return maxLength;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public T getMinValue() {
        return minValue;
    }

    @Override
    protected void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.length() > 1) {
            for (int i = 0; i < str.length(); i++) {
                insertString(offs + i, "" + str.charAt(i), a);
            }
        } else if (str.length() == 1) {
            if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
                document.insertString(offs, str, a);
                return;
            }
            if (str.charAt(0) == MINUS_SIGN) {
                String actual = document.getText(0, document.getLength());
                if (actual.indexOf(str.charAt(0)) == -1 && offs == 0) {
                    document.insertString(offs, str, a);
                }
                return;
            }
        } else {
            document.insertString(offs, str, a);
        }
    }

    @Override
    public boolean isContentNull() {
        return getText().length() == 0;
    }

    @Override
    public int contentValid() {
        if (canBeNull && isContentNull()) {
            return VALID;
        }
        if (!canBeNull && isContentNull()) {
            return NULL;
        }
        try {
            T value = getValue();
            if (value instanceof Comparable) {
                Comparable<T> comparable = (Comparable<T>) value;
                if (getMinValue() != null) {
                    if (comparable.compareTo(getMinValue()) < 0) {
                        return SCOPE;
                    }
                }
                if (getMaxValue() != null) {
                    if (comparable.compareTo(getMaxValue()) > 0) {
                        return SCOPE;
                    }
                }
            }
        } catch (Exception e) {
            return FORMAT;
        }
        return VALID;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        document.remove(offs, len);
    }

    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
        setColumns(getMaxCharCount());
    }

    public void setMinValue(T minValue) {
        this.minValue = minValue;
        setColumns(getMaxCharCount());
    }

}
