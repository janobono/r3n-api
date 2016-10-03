package sk.r3n.ui;

import javax.swing.JFileChooser;

public enum Filter {

    FILES_ONLY(JFileChooser.FILES_ONLY),
    DIRECTORIES_ONLY(JFileChooser.DIRECTORIES_ONLY),
    FILES_AND_DIRECTORIES(JFileChooser.FILES_AND_DIRECTORIES);

    private final int code;

    private Filter(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

}
