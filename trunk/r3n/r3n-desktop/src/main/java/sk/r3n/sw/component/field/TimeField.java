package sk.r3n.sw.component.field;

import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.BadLocationException;
import sk.r3n.sw.util.InputStatus;
import sk.r3n.util.DateUtil;

public class TimeField extends MaskField<Date> {

    protected final String EMPTY;

    protected final char[] MASK;

    protected Date maxValue;

    protected Date minValue;

    protected final char SEPARATOR;

    protected DateFormat timeFormat;

    public TimeField(boolean canBeNull) {
        this(canBeNull, false, ':');
    }

    public TimeField(boolean canBeNull, boolean seconds, char separator) {
        super(canBeNull);
        this.SEPARATOR = separator;
        String pattern;
        if (seconds) {
            EMPTY = "  " + SEPARATOR + "  " + SEPARATOR + "  ";
            MASK = ("00" + SEPARATOR + "00" + SEPARATOR + "00").toCharArray();
            pattern = "HH" + SEPARATOR + "mm" + SEPARATOR + "ss";
        } else {
            EMPTY = "  " + SEPARATOR + "  ";
            MASK = ("00" + SEPARATOR + "00").toCharArray();
            pattern = "HH" + SEPARATOR + "mm";
        }
        setColumns(pattern.length());
        timeFormat = new SimpleDateFormat(pattern);
        timeFormat.setLenient(false);
        setToolTipText(pattern);
        setValue(null);
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
            return DateUtil.getTimeOnly(timeFormat.parse(getText()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isContentNull() {
        return getText().equals(EMPTY);
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
            String hours = getText().substring(0, 2);
            if (!hours.equals("  ")) {
                if (hours.endsWith(" ")) {
                    hours = "0" + hours.charAt(0);
                }
            }
            String minutes = getText().substring(3, 5);
            if (!minutes.equals("  ")) {
                if (minutes.endsWith(" ")) {
                    minutes = "0" + minutes.charAt(0);
                }
            }
            String seconds = null;
            if (EMPTY.length() == 8) {
                seconds = getText().substring(6, 8);
                if (!seconds.equals("  ")) {
                    if (seconds.endsWith(" ")) {
                        seconds = "0" + seconds.charAt(0);
                    }
                }
            }
            String result = hours + SEPARATOR + minutes;
            if (seconds != null) {
                result += SEPARATOR + seconds;
            }
            result = result.replaceAll(" ", "0");
            if (!actual.equals(result)) {
                setText(result);
            }
        }
    }

    public void setMaxValue(Date maxValue) {
        this.maxValue = DateUtil.getTimeOnly(maxValue);
    }

    public void setMinValue(Date minValue) {
        this.minValue = DateUtil.getTimeOnly(minValue);
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
            setText(timeFormat.format(value));
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }
}