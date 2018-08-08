package sk.r3n.sw.component.field;

import sk.r3n.sw.InputStatus;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.io.UnsupportedEncodingException;

public class R3NPasswordField extends R3NField<byte[]> {

    private boolean blocked;

    private byte[] oldPassword;

    private StringBuffer password;

    private boolean passwordChanged;

    private char passwordChar;

    public R3NPasswordField() {
        super(false);
        setColumns(15);
        password = new StringBuffer();
        passwordChanged = false;
        passwordChar = '*';
    }

    protected byte[] getPassword() {
        if (passwordChanged) {
            try {
                return this.password.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return oldPassword;
        }
    }

    @Override
    public byte[] getValue() {
        if (isContentNull()) {
            return null;
        }
        return getPassword();
    }

    @Override
    public void setValue(byte[] value) {
        if (value == null) {
            value = new byte[]{};
        }
        setText(new String(value));
        oldPassword = value;
        this.password = new StringBuffer();
        passwordChanged = false;
    }

    @Override
    protected void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
        if (!blocked) {
            passwordChanged = true;
            password.insert(offset, str.toCharArray());
        }
        String newStr = "";
        for (int i = 0; i < str.length(); i++) {
            newStr += passwordChar;
        }
        document.insertString(offset, newStr, a);
    }

    @Override
    public boolean isContentNull() {
        return getPassword() == null;
    }

    @Override
    public InputStatus inputStatus() {
        return InputStatus.VALID;
    }

    @Override
    protected void remove(int offs, int len) throws BadLocationException {
        if (passwordChanged) {
            password.delete(offs, offs + len);
        }
        document.remove(offs, len);
    }

    public void setPasswordChar(char passwordChar) {
        this.passwordChar = passwordChar;
    }

}
