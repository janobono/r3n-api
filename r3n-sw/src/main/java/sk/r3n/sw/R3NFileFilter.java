package sk.r3n.sw;

import java.io.File;
import java.io.FileFilter;

public abstract class R3NFileFilter implements FileFilter {

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

    public abstract String getDescription();

    public abstract String[] getExtension();
}
