package sk.r3n.sw.util;

import javax.swing.JFileChooser;

public enum Filter {

    DIRECTORIES_ONLY(JFileChooser.DIRECTORIES_ONLY),
    FILES_AND_DIRECTORIES(JFileChooser.FILES_AND_DIRECTORIES),
    FILES_ONLY(JFileChooser.FILES_ONLY);

    private final int code;

    private Filter(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

}
