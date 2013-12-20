package sk.r3n.sw.util;

import java.io.File;
import sk.r3n.ui.Answer;
import sk.r3n.ui.DialogUI;
import sk.r3n.ui.Filter;
import sk.r3n.ui.MessageType;
import sk.r3n.ui.R3NFileFilter;

public class DialogUIImpl implements DialogUI {

    @Override
    public File openFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName) {
        return SwingUtil.openFile(filter, title, defaultDir, filters, fileName);
    }

    @Override
    public File saveFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName) {
        return SwingUtil.saveFile(filter, title, defaultDir, filters, fileName);
    }

    @Override
    public void showMessageDialog(String title, Object message, MessageType messageType) {
        SwingUtil.showMessageDialog(title, message, messageType);
    }

    @Override
    public Answer showYesNoCancelDialog(String title, Object message, MessageType messageType) {
        return SwingUtil.showYesNoCancelDialog(title, message, messageType);
    }

    @Override
    public Answer showYesNoDialog(String title, Object message, MessageType messageType) {
        return SwingUtil.showYesNoDialog(title, message, messageType);
    }
}
