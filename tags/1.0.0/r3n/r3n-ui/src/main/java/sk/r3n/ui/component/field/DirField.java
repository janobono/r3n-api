package sk.r3n.ui.component.field;

import java.io.File;

public class DirField extends VarcharField {

	private static final long serialVersionUID = 6052247659679085320L;

	public DirField() {
		super(1024, false);
	}

	@Override
	public int contentValid() {
		int contentValid = super.contentValid();
		if (contentValid != VALID)
			return contentValid;
		File file = new File(getValue());
		if (file.isDirectory()) {
			return VALID;
		}
		return FORMAT;
	}

}
