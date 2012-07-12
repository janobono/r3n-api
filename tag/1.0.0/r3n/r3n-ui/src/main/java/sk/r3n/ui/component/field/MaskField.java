package sk.r3n.ui.component.field;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public abstract class MaskField<T> extends R3NField<T> {

	private static final long serialVersionUID = -6046662046479522934L;

	public MaskField(boolean canBeNull) {
		super(canBeNull);
	}

	protected abstract char[] getMask();

	protected abstract char getReplacement(int offs);

	protected abstract int getTextLength();

	@Override
	protected void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if (str.length() > 1) {
			// Pridavanie retazca po znakoch
			for (int i = 0; i < str.length(); i++) {
				insertString(offs + i, "" + str.charAt(i), a);
			}
		} else if (str.length() == 1) {
			// Overenie znaku
			if (isInsertable(str.charAt(0))) {
				while (offs < getTextLength()) {
					if (!isMask(offs)) {
						document.remove(offs, 1);
						document.insertString(offs, str, a);
						setCaretPosition(offs + 1);
						return;
					}
					offs++;
				}
			}
			// Overenie ci je separator
			if (isMask(str.charAt(0))) {
				for (int i = offs; i < getMask().length; i++) {
					if (getMask()[i] == str.charAt(0)) {
						setCaretPosition(i + 1);
						return;
					}
				}
			}
		}
	}

	protected abstract boolean isInsertable(char c);

	protected abstract boolean isMask(char c);

	protected abstract boolean isMask(int offs);

	@Override
	protected void remove(int offs, int len) throws BadLocationException {
		if (len > 1) {
			for (int i = 0; i < len; i++) {
				remove(offs + i, 1);
			}
		} else {
			String actual = document.getText(0, document.getLength());
			if (actual.length() == getTextLength()) {
				if (!isMask(offs)) {
					document.remove(offs, 1);
					document.insertString(offs, "" + getReplacement(offs), null);
				}
				setCaretPosition(offs);
			}
		}
	}

}
