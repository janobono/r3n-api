package sk.r3n.ui;

import java.io.File;
import java.util.ResourceBundle;

public class AllFileFilter extends R3NFileFilter {

    protected static final String DESCRIPTION = "DESCRIPTION";

    public AllFileFilter() {
        super(true, false);
    }

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle(AllFileFilter.class.getCanonicalName()).getString(DESCRIPTION);
    }

    @Override
    public boolean accept(File dir, String name) {
        return true;
    }

    @Override
    public String getExtension() {
        return null;
    }
}
