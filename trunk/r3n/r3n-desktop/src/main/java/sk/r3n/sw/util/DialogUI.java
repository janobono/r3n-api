package sk.r3n.sw.util;

import java.io.File;

public interface DialogUI {

    public File openFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    public File saveFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    public void showMessageDialog(String title, Object message, MessageType messageType);

    public Answer showYesNoCancelDialog(String title, Object message, MessageType messageType);

    public Answer showYesNoDialog(String title, Object message, MessageType messageType);

}
