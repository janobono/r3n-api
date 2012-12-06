package sk.r3n.swing.component;

import java.io.File;
import java.io.FileFilter;

public abstract class R3NFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {

    protected boolean dir;

    protected boolean ext;

    public R3NFileFilter(boolean dir, boolean ext) {
        super();
        this.dir = dir;
        this.ext = ext;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory() && dir) {
            return true;
        }
        return accept(f.getParentFile(), f.getName());
    }

    protected abstract boolean accept(File parentFile, String name);

    public abstract String getExtension();

    public String getName(String name) {
        if (name == null) {
            name = "new";
        }

        if (name.equals("")) {
            name = "new";
        }

        if (!ext) {
            return name;
        }

        if (name.equalsIgnoreCase(getExtension())) {
            return "new" + getExtension();
        }

        if (name.lastIndexOf('.') == -1) {
            return name + getExtension();
        }

        return name;
    }

}
