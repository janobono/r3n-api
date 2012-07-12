package sk.r3n.ui.component.field;

import java.io.File;

public class FileField extends VarcharField {

	private static final long serialVersionUID = -5427295910728494357L;

	public FileField() {
		super(1024, false);
	}

	@Override
	public int contentValid() {
		int contentValid = super.contentValid();
		if (contentValid != VALID)
			return contentValid;
		File file = new File(getValue());
		if (file.isFile())
			return VALID;
		return FORMAT;
	}

}
