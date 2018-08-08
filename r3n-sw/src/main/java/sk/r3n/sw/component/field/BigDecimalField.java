package sk.r3n.sw.component.field;

import sk.r3n.sw.InputStatus;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class BigDecimalField extends R3NField<BigDecimal> {

    public final char MINUS_SIGN;
    public final char DECIMAL_SEPARATOR;
    private final short scale;
    protected BigDecimal maxValue;
    protected BigDecimal minValue;

    public BigDecimalField(boolean canBeNull, short scale) {
        super(canBeNull);
        MINUS_SIGN = DecimalFormatSymbols.getInstance(Locale.getDefault()).getMinusSign();
        DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance(Locale.getDefault()).getDecimalSeparator();
        this.scale = scale;
        setMinValue(null);
        setMaxValue(null);
        setHorizontalAlignment(RIGHT);
    }

    public static BigDecimal parseBigDecimal(String string, char separator) {
        String numerics;
        String decimals = "";
        try {
            if (string.indexOf(separator) != -1) {
                numerics = Long.toString(Long.parseLong(string.substring(0, string.indexOf(separator))));
                decimals = string.substring(string.indexOf(separator) + 1);
            } else {
                numerics = string;
            }
            if (numerics.length() != 0) {
                return new BigDecimal(numerics + '.' + decimals);
            }
            return new BigDecimal(numerics);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(BigDecimal value, short scale, char separator) {
        String result = value.toPlainString();
        String numerics = "";
        String decimals = "";
        try {
            if (result.indexOf('.') != -1) {
                numerics = Long.toString(Long.parseLong(result.substring(0, result.indexOf('.'))));
                decimals = result.substring(result.indexOf('.') + 1);
            } else {
                numerics = result;
            }
            if (decimals.length() < scale) {
                for (int i = decimals.length(); i < scale; i++) {
                    decimals += '0';
                }
            } else if (decimals.length() > scale) {
                while (decimals.length() != scale) {
                    decimals = decimals.substring(0, decimals.length() - 1);
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        if (decimals.length() > 0) {
            return numerics + separator + decimals;
        }
        return numerics;
    }

    protected int getMaxCharCount() {
        int minLength = 0;
        if (minValue != null) {
            minLength = minValue.toPlainString().length();
        }
        int maxLength = 0;
        if (maxValue != null) {
            maxLength = maxValue.toPlainString().length();
        }
        if (minLength > maxLength) {
            return minLength;
        }
        return maxLength;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
        setColumns(getMaxCharCount());
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
        setColumns(getMaxCharCount());
    }

    @Override
    public BigDecimal getValue() {
        if (isContentNull()) {
            return null;
        }
        BigDecimal result = parseBigDecimal(getText(), DECIMAL_SEPARATOR);
        if (result.scale() > scale) {
            throw new RuntimeException("WRONG SCALE!");
        }
        return result;
    }

    @Override
    public void setValue(BigDecimal value) {
        if (value == null) {
            setText("");
        } else {
            setText(toString(value, scale, DECIMAL_SEPARATOR));
        }
    }

    @Override
    protected void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.length() > 1) {
            // add string char by char
            for (int i = 0; i < str.length(); i++) {
                insertString(offs + i, "" + str.charAt(i), a);
            }
        } else if (str.length() == 1) {
            // numeric char
            if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
                document.insertString(offs, str, a);
                return;
            }
            // sign
            if (str.charAt(0) == MINUS_SIGN) {
                String actual = document.getText(0, document.getLength());
                if (actual.indexOf(str.charAt(0)) == -1 && offs == 0) {
                    document.insertString(offs, str, a);
                }
                return;
            }
            // decimal separator
            if (str.charAt(0) == DECIMAL_SEPARATOR) {
                String actual = document.getText(0, document.getLength());
                if (actual.indexOf(str.charAt(0)) == -1 && scale != 0) {
                    if (offs == 0) {
                        document.insertString(offs, "0", a);
                        offs++;
                    }
                    document.insertString(offs, str, a);
                    setCaretPosition(offs + 1);
                }
            }
        } else {
            document.insertString(offs, str, a);
        }
    }

    @Override
    public boolean isContentNull() {
        return getText().equals("");
    }

    @Override
    public InputStatus inputStatus() {
        if (isEnabled() && isEditable() && isVisible()) {
            if (canBeNull && isContentNull()) {
                return InputStatus.VALID;
            }
            if (!canBeNull && isContentNull()) {
                return InputStatus.NULL;
            }
            try {
                BigDecimal value = getValue();
                if (getMinValue() != null) {
                    if (value.compareTo(getMinValue()) < 0) {
                        return InputStatus.SCOPE;
                    }
                }
                if (getMaxValue() != null) {
                    if (value.compareTo(getMaxValue()) > 0) {
                        return InputStatus.SCOPE;
                    }
                }
            } catch (Exception e) {
                return InputStatus.FORMAT;
            }
        }
        return InputStatus.VALID;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        document.remove(offs, len);
    }

}
