package sk.r3n.sw.util;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.*;
import sk.r3n.ui.component.R3NFileFilter;
import sk.r3n.ui.component.R3NInputComponent;

public interface UIService {





    public int FILE_DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;

    public int FILE_FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

    public int FILE_FILES_ONLY = JFileChooser.FILES_ONLY;



    public String getActionMapKey(String groupId, int actionId);

    public Frame getFrameForComponent(Component component);

    public Icon getIcon(URL url);

    public Icon getIcon(URL url, Dimension dimension);

    public Image getImage(URL url);

    public byte[] getPassword(String title);

    public Frame getRootFrame();

    public boolean isInputValid(List<R3NInputComponent<?>> inputComponents);

    public void modifyDimensions(Window window);

    public void modifyFocus(Component component);

    public void modifyFontSize(float coefficient);

    public File openFile(int filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    public void positionCenterScreen(Window window);

    public void positionCenterWindow(Window parent, Window window);

    public File saveFile(int filter, String title, File defaultDir, R3NFileFilter[] filters, String fileName);

    public void setDimension(String key, Dimension dimension);

    public void setMaxDimension(Dimension max);

    public void setRecount(String key, Boolean recount);

    public void setRootFrame(Frame frame);

    public void showMessageDialog(String title, Object message, int messageAction);

    public int showYesNoCancelDialog(String title, Object message, int messageAction);

    public int showYesNoDialog(String title, Object message, int messageAction);

    public void statusAutoProgress(long id);

    public void statusFinishProgress(long id);

    public void statusHide(long id);

    public void statusIncrementProgress(long id);

    public void statusSetText(long id, String text);

    public void statusSetTitle(long id, String title);

    public long statusShow(String title);

    public void statusStartProgress(long id);

}
