package sk.r3n.ui.component.field;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import sk.r3n.util.ByteArray;

public class ByteField extends R3NField<Byte> {

    private final String BIN_PREFIX;

    public ByteField(boolean canBeNull) {
        this(canBeNull, "0x");
    }

    public ByteField(boolean canBeNull, String binPrefix) {
        super(canBeNull);
        this.BIN_PREFIX = binPrefix;
        setColumns(7);
    }

    @Override
    public Byte getValue() {
        if (isContentNull()) {
            return null;
        }
        String text = getText();
        text = text.trim();
        return ByteArray.parseByte(text);
    }

    @Override
    protected void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str.length() > 1) {
            // Pridavanie retazca po znakoch
            for (int i = 0; i < str.length(); i++) {
                insertString(offs + i, "" + str.charAt(i), a);
            }
        } else if (str.length() == 1) {
            // platny znak
            try {
                ByteArray.charToHexa(str.charAt(0));
                document.insertString(offs, str, a);
                return;
            } catch (Exception e) {
            }
            if (BIN_PREFIX.startsWith(str) || BIN_PREFIX.endsWith(str)) {
                document.insertString(offs, str, a);
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
            getValue();
        } catch (Exception e) {
            return FORMAT;
        }
        return VALID;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        document.remove(offs, len);
    }

    @Override
    public void setValue(Byte value) {
        if (value == null) {
            setText("");
        } else {
            setText(ByteArray.toString(value));
        }
    }
}