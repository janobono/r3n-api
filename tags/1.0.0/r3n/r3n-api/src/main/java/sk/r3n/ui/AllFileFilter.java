package sk.r3n.ui;

import java.io.File;
import java.util.ResourceBundle;

public class AllFileFilter extends R3NFileFilter {

	public AllFileFilter() {
		super(true, false);
	}

	@Override
	public String getDescription() {
		return ResourceBundle.getBundle(this.getClass().getCanonicalName())
				.getString("DESCRIPTION");
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
