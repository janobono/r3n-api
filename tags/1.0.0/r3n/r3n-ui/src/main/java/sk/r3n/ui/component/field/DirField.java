package sk.r3n.ui.component.field;

import java.io.File;

public class DirField extends VarcharField {

    public DirField() {
        super(1024, false);
    }

    @Override
    public int contentValid() {
        int contentValid = super.contentValid();
        if (contentValid != VALID) {
            return contentValid;
        }
        File file = new File(getValue());
        if (file.isDirectory()) {
            return VALID;
        }
        return FORMAT;
    }
}
