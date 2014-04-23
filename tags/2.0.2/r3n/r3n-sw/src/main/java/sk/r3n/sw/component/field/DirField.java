package sk.r3n.sw.component.field;

import java.io.File;
import sk.r3n.ui.InputStatus;

public class DirField extends VarcharField {

    public DirField() {
        super(1024, false);
    }

    @Override
    public InputStatus inputStatus() {
        InputStatus inputStatus = super.inputStatus();
        if (!inputStatus.equals(InputStatus.VALID)) {
            return inputStatus;
        }
        File file = new File(getValue());
        if (file.isDirectory()) {
            return InputStatus.VALID;
        }
        return InputStatus.FORMAT;
    }

}
