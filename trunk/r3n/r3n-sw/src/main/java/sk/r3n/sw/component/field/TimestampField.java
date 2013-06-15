package sk.r3n.sw.component.field;

import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import sk.r3n.ui.InputStatus;

public class TimestampField extends MaskField<Date> {

    protected final String EMPTY;

    protected final char[] MASK;

    protected Date maxValue;

    protected Date minValue;

    protected final char SEPARATOR1;

    protected final char SEPARATOR2;

    protected final char SEPARATOR3;

    protected DateFormat timestampFormat;

    public TimestampField(boolean canBeNull) {
        this(canBeNull, false, '.', ' ', ':');
    }

    public TimestampField(boolean canBeNull, boolean seconds,
            char dateSeparator, char dateTimeSeparator, char timeSeparator) {
        super(canBeNull);
        this.SEPARATOR1 = dateSeparator;
        this.SEPARATOR2 = dateTimeSeparator;
        this.SEPARATOR3 = timeSeparator;
        String pattern;
        if (seconds) {
            EMPTY = "  " + SEPARATOR1 + "  " + SEPARATOR1 + "    " + SEPARATOR2
                    + "  " + SEPARATOR3 + "  " + SEPARATOR3 + "  ";
            MASK = ("00" + SEPARATOR1 + "00" + SEPARATOR1 + "0000" + SEPARATOR2
                    + "00" + SEPARATOR3 + "00" + SEPARATOR3 + "00").toCharArray();
            pattern = "dd" + SEPARATOR1 + "MM" + SEPARATOR1 + "yyyy"
                    + SEPARATOR2 + "HH" + SEPARATOR3 + "mm" + SEPARATOR3 + "ss";
        } else {
            EMPTY = "  " + SEPARATOR1 + "  " + SEPARATOR1 + "    " + SEPARATOR2
                    + "  " + SEPARATOR3 + "  ";
            MASK = ("00" + SEPARATOR1 + "00" + SEPARATOR1 + "0000" + SEPARATOR2
                    + "00" + SEPARATOR3 + "00").toCharArray();
            pattern = "dd" + SEPARATOR1 + "MM" + SEPARATOR1 + "yyyy"
                    + SEPARATOR2 + "HH" + SEPARATOR3 + "mm";
        }
        setColumns(pattern.length());
        timestampFormat = new SimpleDateFormat(pattern);
        timestampFormat.setLenient(false);
        setToolTipText(pattern);
        setValue(null);
    }

    @Override
    public void focusGained(FocusEvent e) {
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
            return timestampFormat.parse(getText());
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
        return c == SEPARATOR1 || c == SEPARATOR2 || c == SEPARATOR3;
    }

    @Override
    protected boolean isMask(int offs) {
        return offs == 2 || offs == 5 || offs == 10 || offs == 13 || offs == 16;
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
            String hours = getText().substring(11, 13);
            if (!hours.equals("  ")) {
                if (hours.endsWith(" ")) {
                    hours = "0" + hours.charAt(0);
                }
            }
            String minutes = getText().substring(14, 16);
            if (!minutes.equals("  ")) {
                if (minutes.endsWith(" ")) {
                    minutes = "0" + minutes.charAt(0);
                }
            }
            String seconds = null;
            if (EMPTY.length() == 19) {
                seconds = getText().substring(17, 19);
                if (!seconds.equals("  ")) {
                    if (seconds.endsWith(" ")) {
                        seconds = "0" + seconds.charAt(0);
                    }
                }
            }
            String result = day + SEPARATOR1 + month + SEPARATOR1 + year
                    + SEPARATOR2 + hours + SEPARATOR3 + minutes;
            if (seconds != null) {
                result += SEPARATOR3 + seconds;
            }
            result = result.replaceAll(" ", "0");
            if (!actual.equals(result)) {
                setText(result);
            }
        }
    }

    public void setMaxValue(Date maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(Date minValue) {
        this.minValue = minValue;
    }

    @Override
    public void setValue(Date value) {
        if (value == null) {
            try {
                document.remove(0, document.getLength());
                document.insertString(0, EMPTY, null);
            } catch (Exception e) {
            }
        } else {
            setText(timestampFormat.format(value));
        }
    }
}
