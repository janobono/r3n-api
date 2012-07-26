package sk.r3n.ui.component.field;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import sk.r3n.ui.R3NInputComponent;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NField<T> extends JTextField implements
        R3NInputComponent<T>, FocusListener {

    public class BaseDocument implements Document {

        @Override
        public void addDocumentListener(DocumentListener listener) {
            document.addDocumentListener(listener);
        }

        @Override
        public void addUndoableEditListener(UndoableEditListener listener) {
            document.addUndoableEditListener(listener);
        }

        @Override
        public Position createPosition(int offs) throws BadLocationException {
            return document.createPosition(offs);
        }

        @Override
        public Element getDefaultRootElement() {
            return document.getDefaultRootElement();
        }

        @Override
        public Position getEndPosition() {
            return document.getEndPosition();
        }

        @Override
        public int getLength() {
            return document.getLength();
        }

        @Override
        public Object getProperty(Object key) {
            return document.getProperty(key);
        }

        @Override
        public Element[] getRootElements() {
            return document.getRootElements();
        }

        @Override
        public Position getStartPosition() {
            return document.getStartPosition();
        }

        @Override
        public String getText(int offset, int length)
                throws BadLocationException {
            return document.getText(offset, length);
        }

        @Override
        public void getText(int offset, int length, Segment txt)
                throws BadLocationException {
            document.getText(offset, length, txt);
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr)
                throws BadLocationException {
            R3NField.this.insertString(offset, str, attr);
        }

        @Override
        public void putProperty(Object key, Object value) {
            document.putProperty(key, value);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            R3NField.this.remove(offs, len);
        }

        @Override
        public void removeDocumentListener(DocumentListener listener) {
            document.removeDocumentListener(listener);
        }

        @Override
        public void removeUndoableEditListener(UndoableEditListener listener) {
            document.removeUndoableEditListener(listener);
        }

        @Override
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

            @Override
            public void actionPerformed(ActionEvent e) {
                actionCellOk();
            }

            @Override
            public void addPropertyChangeListener(
                    PropertyChangeListener listener) {
            }

            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void putValue(String key, Object value) {
            }

            @Override
            public void removePropertyChangeListener(
                    PropertyChangeListener listener) {
            }

            @Override
            public void setEnabled(boolean b) {
            }
        });
        // Fokus
        UIServiceManager.getDefaultUIService().modifyFocus(this);
    }

    protected void actionCellOk() {
        transferFocus();
    }

    @Override
    public void focusGained(FocusEvent e) {
        selectAll();
    }

    @Override
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
