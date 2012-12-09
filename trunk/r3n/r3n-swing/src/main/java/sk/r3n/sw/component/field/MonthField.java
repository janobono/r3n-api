package sk.r3n.sw.component.field;

import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MonthField extends MaskField<Date> {

    protected DateFormat dateFormat;

    protected final String EMPTY;

    protected final char[] MASK;

    protected final char SEPARATOR;

    protected Date maxValue;

    protected Date minValue;

    public MonthField(boolean canBeNull) {
        this(canBeNull, ' ');
    }

    public MonthField(boolean canBeNull, char separator) {
        super(canBeNull);
        this.SEPARATOR = separator;
        EMPTY = "  " + SEPARATOR + "    ";
        MASK = ("00" + SEPARATOR + "0000").toCharArray();
        String pattern = "MM" + SEPARATOR + "yyyy";
        dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        setToolTipText(pattern);
        setColumns(6);
        setValue(null);
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
            Date value = getValue();
            if (getMinValue() != null) {
                if (value.compareTo(getMinValue()) < 0) {
                    return SCOPE;
                }
            }
            if (getMaxValue() != null) {
                if (value.compareTo(getMaxValue()) > 0) {
                    return SCOPE;
                }
            }
        } catch (Exception e) {
            return FORMAT;
        }
        return VALID;
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
            return modifyDate(dateFormat.parse(getText()));
        } catch (Exception e) {
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
        return offs == 2;
    }

    public Date modifyDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private void reFormatInput() {
        if (!isContentNull()) {
            String actual = getText();
            String month = getText().substring(0, 2);
            if (!month.equals("  ")) {
                if (month.endsWith(" ")) {
                    month = "0" + month.charAt(0);
                }
            }
            String year = getText().substring(3, 7);
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
            String formatted = (month + SEPARATOR + year).replaceAll(" ", "0");
            if (!actual.equals(formatted)) {
                setText(formatted);
            }
        }
    }

    public void setMaxValue(Date maxValue) {
        this.maxValue = modifyDate(maxValue);
    }

    public void setMinValue(Date minValue) {
        this.minValue = modifyDate(minValue);
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
            setText(dateFormat.format(value));
        }
    }

}
