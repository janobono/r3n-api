package sk.r3n.sw.component.field;

import sk.r3n.sw.InputStatus;

import java.io.File;

public class FileField extends VarcharField {

    public FileField() {
        super(1024, false);
    }

    @Override
    public InputStatus inputStatus() {
        InputStatus inputStatus = super.inputStatus();
        if (!inputStatus.equals(InputStatus.VALID)) {
            return inputStatus;
        }
        File file = new File(getValue());
        if (file.isFile()) {
            return InputStatus.VALID;
        }
        return InputStatus.FORMAT;
    }

}
