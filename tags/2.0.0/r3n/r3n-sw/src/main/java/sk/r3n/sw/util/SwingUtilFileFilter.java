package sk.r3n.sw.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import sk.r3n.ui.R3NFileFilter;

public class SwingUtilFileFilter extends FileFilter {

    private R3NFileFilter fileFilter;

    public SwingUtilFileFilter(R3NFileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    @Override
    public boolean accept(File f) {
        return fileFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return fileFilter.getDescription();
    }

    public R3NFileFilter getFileFilter() {
        return fileFilter;
    }

}
