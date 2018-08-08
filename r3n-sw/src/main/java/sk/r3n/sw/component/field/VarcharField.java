package sk.r3n.sw.component.field;

import sk.r3n.sw.InputStatus;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class VarcharField extends R3NField<String> {

    protected int size;

    protected boolean overSize;

    protected boolean trim;

    public VarcharField(boolean canBeNull) {
        this(256, canBeNull);
    }

    public VarcharField(int size, boolean canBeNull) {
        super(canBeNull);
        setSize(size, true);
        overSize = true;
        trim = false;
    }

    @Override
    public InputStatus inputStatus() {
        if (canBeNull && isContentNull()) {
            return InputStatus.VALID;
        }
        if (!canBeNull && isContentNull()) {
            return InputStatus.NULL;
        }
        if (getText().length() > size) {
            return InputStatus.SIZE;
        }
        return InputStatus.VALID;
    }

    @Override
    public String getValue() {
        if (isContentNull()) {
            return null;
        }
        if (getText().length() > size) {
            throw new RuntimeException("WRONG SIZE!");
        }
        if (trim) {
            return getText().trim();
        }
        return getText();
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            setText("");
        } else {
            setText(value);
        }
    }

    @Override
    protected void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (document.getLength() + str.length() > size && !overSize) {
            return;
        }
        document.insertString(offset, str, a);
    }

    @Override
    public boolean isContentNull() {
        String text = getText();
        if (trim) {
            text = text.trim();
        }
        return text.length() == 0;
    }

    public boolean isOverSize() {
        return overSize;
    }

    public void setOverSize(boolean overSize) {
        this.overSize = overSize;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        document.remove(offs, len);
    }

    public void setSize(int size, boolean modifyColumns) {
        if (size < 0) {
            this.size = 0;
        } else {
            this.size = size;
        }
        if (modifyColumns) {
            setColumns(size);
        }
    }

}
