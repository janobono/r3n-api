package sk.r3n.sw.component.field;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import sk.r3n.ui.InputComponent;

public abstract class R3NField<T> extends JTextField implements InputComponent<T> {

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
        public String getText(int offset, int length) throws BadLocationException {
            return document.getText(offset, length);
        }

        @Override
        public void getText(int offset, int length, Segment txt) throws BadLocationException {
            document.getText(offset, length, txt);
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
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
        document = getDocument();
        setDocument(new BaseDocument());
    }

    protected void actionCellOk() {
        transferFocus();
    }

    protected abstract void insertString(int offs, String str, AttributeSet a) throws BadLocationException;

    protected abstract void remove(int offs, int len) throws BadLocationException;

    public void setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    @Override
    public void setColumns(int columns) {
        super.setColumns(columns);
        setMinimumSize(getPreferredSize());
    }

}
