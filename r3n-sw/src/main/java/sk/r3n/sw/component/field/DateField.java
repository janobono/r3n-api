package sk.r3n.sw.component.field;

import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.BadLocationException;
import sk.r3n.ui.InputStatus;
import sk.r3n.util.DateUtil;

public class DateField extends MaskField<Date> {

    protected DateFormat dateFormat;

    protected final String EMPTY;

    protected final char[] MASK;

    protected final char SEPARATOR;

    protected Date maxValue;

    protected Date minValue;

    public DateField(boolean canBeNull) {
        this(canBeNull, '.');
    }

    public DateField(boolean canBeNull, char separator) {
        super(canBeNull);
        this.SEPARATOR = separator;
        EMPTY = "  " + SEPARATOR + "  " + SEPARATOR + "    ";
        MASK = ("00" + SEPARATOR + "00" + SEPARATOR + "0000").toCharArray();
        String pattern = "dd" + SEPARATOR + "MM" + SEPARATOR + "yyyy";
        dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        setToolTipText(pattern);
        setColumns(pattern.length());
        setValue(null);
    }

    @Override
    public InputStatus inputStatus() {
        if (canBeNull && isContentNull()) {
            return InputStatus.VALID;
        }
        if (!canBeNull && isContentNull()) {
            return InputStatus.NULL;
        }
        try {
            Date value = getValue();
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
        return InputStatus.VALID;
    }

    @Override
    public void focusLost(FocusEvent e) {
        reFormatInput();
    }

    @Override
    protected char[] getMask() {
        return MASK;
    }

    public Date getMaxValue() {
        return maxValue;
    }

    public Date getMinValue() {
        return minValue;
    }

    @Override
    protected char getReplacement(int offs) {
        return ' ';
    }

    @Override
    protected int getTextLength() {
        return MASK.length;
    }

    @Override
    public Date getValue() {
        if (isContentNull()) {
            return null;
        }
        try {
            reFormatInput();
            return DateUtil.getDateOnly(dateFormat.parse(getText()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isContentNull() {
        return getText().equals(EMPTY);
    }

    @Override
    protected boolean isInsertable(char c) {
        return c >= '0' && c <= '9';
    }

    @Override
    protected boolean isMask(char c) {
        return c == SEPARATOR;
    }

    @Override
    protected boolean isMask(int offs) {
        return offs == 2 || offs == 5;
    }

    private void reFormatInput() {
        if (!isContentNull()) {
            String actual = getText();
            String day = getText().substring(0, 2);
            if (!day.equals("  ")) {
                if (day.endsWith(" ")) {
                    day = "0" + day.charAt(0);
                }
            }
            String month = getText().substring(3, 5);
            if (!month.equals("  ")) {
                if (month.endsWith(" ")) {
                    month = "0" + month.charAt(0);
                }
            }
            String year = getText().substring(6, 10);
            if (!year.equals("    ")) {
                if (year.endsWith("   ")) {
                    year = "200" + year.charAt(0);
                }
                if (year.endsWith("  ")) {
                    year = "20" + year.substring(0, 2);
                }
                if (year.endsWith(" ")) {
                    year = "2" + year.substring(0, 3);
                }
                if (year.startsWith(" ")) {
                    year = "2" + year.substring(1, 4);
                }
            }
            String formatted = (day + SEPARATOR + month + SEPARATOR + year).replaceAll(" ", "0");
            if (!actual.equals(formatted)) {
                setText(formatted);
            }
        }
    }

    public void setMaxValue(Date maxValue) {
        this.maxValue = DateUtil.getDateOnly(maxValue);
    }

    public void setMinValue(Date minValue) {
        this.minValue = DateUtil.getDateOnly(minValue);
    }

    @Override
    public void setValue(Date value) {
        if (value == null) {
            try {
                document.remove(0, document.getLength());
                document.insertString(0, EMPTY, null);
            } catch (BadLocationException e) {
            }
        } else {
            setText(dateFormat.format(value));
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }
}
