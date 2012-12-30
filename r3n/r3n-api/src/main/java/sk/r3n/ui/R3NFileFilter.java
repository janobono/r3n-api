package sk.r3n.ui;

import java.io.File;
import java.io.FileFilter;
import sk.r3n.util.BundleResolver;

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
            name = BundleResolver.resolve(R3NFileFilter.class.getCanonicalName(), NEW);
        }

        if (!ext) {
            return name;
        }

        if (name.equalsIgnoreCase(getExtension())) {
            return BundleResolver.resolve(R3NFileFilter.class.getCanonicalName(), NEW) + getExtension();
        }

        if (name.lastIndexOf('.') == -1) {
            return name + getExtension();
        }

        return name;
    }

}
