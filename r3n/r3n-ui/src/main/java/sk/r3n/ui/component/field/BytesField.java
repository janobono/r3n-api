package sk.r3n.ui.component.field;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import sk.r3n.util.ByteArray;

public class BytesField extends R3NField<byte[]> {

    private final char ARRAY_SEPARATOR;
    private final String BIN_PREFIX;

    public BytesField(boolean canBeNull) {
        this(canBeNull, ' ', "0x");
    }

    public BytesField(boolean canBeNull, char arraySeparator, String binPrefix) {
        super(canBeNull);
        this.ARRAY_SEPARATOR = arraySeparator;
        this.BIN_PREFIX = binPrefix;
        setColumns(20);
    }

    public byte[] getValue() {
        if (isContentNull()) {
            return null;
        }
        String text = getText();
        text = text.trim();
        text = text.replace(' ', ARRAY_SEPARATOR);
        text = ARRAY_SEPARATOR + text + ARRAY_SEPARATOR;
        return ByteArray.parseBytes(text);
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
            if (str.charAt(0) == ' ') {
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
    public void setValue(byte[] value) {
        if (value == null) {
            setText("");
        } else {
            String text = ByteArray.toString(value);
            // if (text.length() >= 2) {
            // text = text.substring(1, text.length() - 1);
            // text = text.replace(ARRAY_SEPARATOR, ' ');
            // }
            setText(text);
        }
    }
}
