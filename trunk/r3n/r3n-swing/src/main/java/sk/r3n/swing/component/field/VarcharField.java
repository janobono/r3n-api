package sk.r3n.swing.component.field;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import sk.r3n.ui.util.UIServiceManager;

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
    public int contentValid() {
        if (canBeNull && isContentNull()) {
            return VALID;
        }
        if (!canBeNull && isContentNull()) {
            return NULL;
        }
        if (getText().length() > size) {
            return SIZE;
        }
        return VALID;
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
    protected void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (document.getLength() + str.length() > size && !overSize) {
            UIServiceManager.getDefaultUIService().getBuzzer().buzz(this);
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

    public boolean isTrim() {
        return trim;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        document.remove(offs, len);
    }

    public void setOverSize(boolean overSize) {
        this.overSize = overSize;
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

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            setText("");
        } else {
            setText(value);
        }
    }

}
