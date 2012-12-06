package sk.r3n.swing.component.field;

import java.io.File;

public class FileField extends VarcharField {

    public FileField() {
        super(1024, false);
    }

    @Override
    public int contentValid() {
        int contentValid = super.contentValid();
        if (contentValid != VALID) {
            return contentValid;
        }
        File file = new File(getValue());
        if (file.isFile()) {
            return VALID;
        }
        return FORMAT;
    }

}
