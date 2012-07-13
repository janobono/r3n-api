package sk.r3n.ui.component.field;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import sk.r3n.ui.R3NInputComponent;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NField<T> extends JTextField implements
		R3NInputComponent<T>, FocusListener {

	private static final long serialVersionUID = -5426973221741219679L;

	public class BaseDocument implements Document {

		public void addDocumentListener(DocumentListener listener) {
			document.addDocumentListener(listener);
		}

		public void addUndoableEditListener(UndoableEditListener listener) {
			document.addUndoableEditListener(listener);
		}

		public Position createPosition(int offs) throws BadLocationException {
			return document.createPosition(offs);
		}

		public Element getDefaultRootElement() {
			return document.getDefaultRootElement();
		}

		public Position getEndPosition() {
			return document.getEndPosition();
		}

		public int getLength() {
			return document.getLength();
		}

		public Object getProperty(Object key) {
			return document.getProperty(key);
		}

		public Element[] getRootElements() {
			return document.getRootElements();
		}

		public Position getStartPosition() {
			return document.getStartPosition();
		}

		public String getText(int offset, int length)
				throws BadLocationException {
			return document.getText(offset, length);
		}

		public void getText(int offset, int length, Segment txt)
				throws BadLocationException {
			document.getText(offset, length, txt);
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {
			R3NField.this.insertString(offset, str, attr);
		}

		public void putProperty(Object key, Object value) {
			document.putProperty(key, value);
		}

		public void remove(int offs, int len) throws BadLocationException {
			R3NField.this.remove(offs, len);
		}

		public void removeDocumentListener(DocumentListener listener) {
			document.removeDocumentListener(listener);
		}

		public void removeUndoableEditListener(UndoableEditListener listener) {
			document.removeUndoableEditListener(listener);
		}

		public void render(Runnable r) {
			document.render(r);
		}

	}

	protected boolean canBeNull;

	protected Document document;

	public R3NField(boolean canBeNull) {
		super();
		this.canBeNull = canBeNull;
		addFocusListener(this);
		document = getDocument();
		setDocument(new BaseDocument());
		// Modifikacia klavesovych skratiek
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
				WHEN_FOCUSED, this, new Action() {

					public void actionPerformed(ActionEvent e) {
						actionCellOk();
					}

					public void addPropertyChangeListener(
							PropertyChangeListener listener) {
					}

					public Object getValue(String key) {
						return null;
					}

					public boolean isEnabled() {
						return true;
					}

					public void putValue(String key, Object value) {
					}

					public void removePropertyChangeListener(
							PropertyChangeListener listener) {
					}

					public void setEnabled(boolean b) {
					}
				});
		// Fokus
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

	protected void actionCellOk() {
		transferFocus();
	}

	public void focusGained(FocusEvent e) {
		selectAll();
	}

	public void focusLost(FocusEvent e) {
	}

	protected abstract void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException;

	protected abstract void remove(int offs, int len)
			throws BadLocationException;

	public void setCanBeNull(boolean canBeNull) {
		this.canBeNull = canBeNull;
	}

	@Override
	public void setColumns(int columns) {
		super.setColumns(columns);
		setMinimumSize(getPreferredSize());
	}

}
