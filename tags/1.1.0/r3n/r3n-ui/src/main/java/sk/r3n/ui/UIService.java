package sk.r3n.ui;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.*;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.component.R3NFileFilter;
import sk.r3n.ui.component.R3NInputComponent;

public interface UIService {

    public int ACTION_CLOSE = 0;
    public int ACTION_RESTART = 1;
    public int ACTION_REFRESH = 2;
    public int ACTION_OK = 10;
    public int ACTION_CANCEL = 11;
    public int ACTION_YES = 12;
    public int ACTION_NO = 13;
    public int ACTION_UP = 20;
    public int ACTION_DOWN = 21;
    public int ACTION_LEFT = 22;
    public int ACTION_RIGHT = 23;
    public int ACTION_MOVE_UP = 30;
    public int ACTION_MOVE_DOWN = 31;
    public int ACTION_MOVE_LEFT = 32;
    public int ACTION_MOVE_RIGHT = 33;
    public int ACTION_FIRST = 40;
    public int ACTION_PREVIOUS = 41;
    public int ACTION_NEXT = 42;
    public int ACTION_LAST = 43;
    public int ACTION_PREVIOUS_ROWS = 45;
    public int ACTION_NEXT_ROWS = 46;
    public int ACTION_BUTTON = 50;
    public int FOCUS_FORWARD = 60;
    public int FOCUS_BACKWARD = 61;
    public int ACTION_ADD = 70;
    public int ACTION_COPY = 71;
    public int ACTION_EDIT = 72;
    public int ACTION_REMOVE = 73;
    public int ACTION_ADD_TO_LIST = 80;
    public int ACTION_EDIT_ON_LIST = 81;
    public int ACTION_REMOVE_FROM_LIST = 82;
    public int ACTION_CELL_EDIT = 90;
    public int ACTION_CELL_OK = 91;
    public int ACTION_CELL_CANCEL = 92;
    public int ACTION_INFO = 100;
    public int ACTION_WARNING = 101;
    public int ACTION_ERROR = 102;
    public int ACTION_QUESTION = 103;
    public int ACTION_SELECT = 110;
    public int ACTION_PREVIEW = 111;
    public int ACTION_PRINT = 112;
    public int ACTION_PROPERTIES = 120;
    public int ACTION_SEARCH = 130;
    public int ACTION_SWITCH_SEARCH_KEY = 131;
    public int ACTION_FILE_NEW = 140;
    public int ACTION_FILE_OPEN = 141;
    public int ACTION_FILE_SAVE = 142;
    public int ACTION_FILE_SAVE_AS = 143;
    public int ACTION_FILE_DELETE = 144;
    public int ACTION_DIR_NEW = 150;
    public int ACTION_DIR_OPEN = 151;
    public int ACTION_DIR_DELETE = 152;
    public int ACTION_DEFAULT = 160;
    public int ACTION_ENABLE = 165;
    public int ACTION_DISABLE = 166;
    public int ACTION_ABOUT = 170;
    public int ACTION_HELP = 171;
    public int ACTION_LICENSE = 172;
    public int ACTION_LOG_PREVIEW = 173;
    public int ANSWER_OK = ACTION_OK;
    public int ANSWER_CANCEL = ACTION_CANCEL;
    public int ANSWER_YES = ACTION_YES;
    public int ANSWER_NO = ACTION_NO;
    
    public int FILE_DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;
    public int FILE_FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;
    public int FILE_FILES_ONLY = JFileChooser.FILES_ONLY;
    
    public int MESSAGE_ACTION_ERROR = ACTION_ERROR;
    public int MESSAGE_ACTION_INFORMATION = ACTION_INFO;
    public int MESSAGE_ACTION_QUESTION = ACTION_QUESTION;
    public int MESSAGE_ACTION_WARNING = ACTION_WARNING;

    public String getActionMapKey(String groupId, int actionId);

    public Buzzer getBuzzer();

    public Frame getFrameForComponent(Component component);

    public Icon getIcon(URL url);

    public Icon getIcon(URL url, Dimension dimension);

    public IdActionService getIdActionService();

    public Image getImage(URL url);

    public byte[] getPassword(String title);

    public Frame getRootFrame();

    public boolean isInputValid(List<R3NInputComponent<?>> inputComponents);

    public void modifyDimensions(Window window);

    public void modifyFocus(Component component);

    public void modifyFontSize(float coefficient);

    public File openFile(int filter, String title, File defaultDir,
            R3NFileFilter[] filters, String fileName);

    public void positionCenterScreen(Window window);

    public void positionCenterWindow(Window parent, Window window);

    public File saveFile(int filter, String title, File defaultDir,
            R3NFileFilter[] filters, String fileName);

    public void setBuzzer(Buzzer buzzer);

    public void setDimension(String key, Dimension dimension);

    public void setKeyStroke(String groupId, int actionId, InputMap map,
            Object actionKey);

    public void setKeyStroke(String groupId, int actionId, int condition,
            JComponent component, Action action);

    public void setKeyStroke(String groupId, int actionId, int condition,
            JComponent component, IdActionExecutor idActionExecutor);

    public void setMaxDimension(Dimension max);

    public void setRecount(String key, Boolean recount);

    public void setRootFrame(Frame frame);

    public void showMessageDialog(String title, Object message,
            int messageAction);

    public int showYesNoCancelDialog(String title, Object message,
            int messageAction);

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
