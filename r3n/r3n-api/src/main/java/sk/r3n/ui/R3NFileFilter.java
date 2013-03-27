package sk.r3n.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ResourceBundle;

public abstract class R3NFileFilter implements FileFilter {

    private static final String NEW = "NEW";

    protected boolean dir;

    protected boolean ext;

    public R3NFileFilter(boolean dir, boolean ext) {
        super();
        this.dir = dir;
        this.ext = ext;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() && dir) {
            return true;
        }
        return accept(file.getParentFile(), file.getName());
    }

    protected abstract boolean accept(File parentFile, String name);

    public abstract String getExtension();

    public abstract String getDescription();

    public String getName(String name) {
        if (name == null || name.equals("")) {
            name = ResourceBundle.getBundle(R3NFileFilter.class.getCanonicalName()).getString(NEW);
        }

        if (!ext) {
            return name;
        }

        if (name.equalsIgnoreCase(getExtension())) {
            return ResourceBundle.getBundle(R3NFileFilter.class.getCanonicalName()).getString(NEW) + getExtension();
        }

        if (name.lastIndexOf('.') == -1) {
            return name + getExtension();
        }

        return name;
    }
}
