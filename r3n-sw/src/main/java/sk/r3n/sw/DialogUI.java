package sk.r3n.sw;

import java.io.File;

public interface DialogUI {

    File openFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    File saveFile(Filter filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    void showMessageDialog(String title, Object message, MessageType messageType);

    Answer showYesNoCancelDialog(String title, Object message, MessageType messageType);

    Answer showYesNoDialog(String title, Object message, MessageType messageType);
}
