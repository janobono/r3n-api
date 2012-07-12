package sk.r3n.ui;

import java.io.File;

public class ExtensionFileFilter extends R3NFileFilter {

	protected boolean ignoreCase;

	protected String extension;

	protected String description;

	public ExtensionFileFilter(String extension, String description) {
		this(true, extension, description);
	}

	public ExtensionFileFilter(boolean ignoreCase, String extension,
			String description) {
		super(true, true);
		this.ignoreCase = ignoreCase;
		this.extension = extension;
		this.description = description;
	}

	@Override
	protected boolean accept(File parentFile, String name) {
		if (ignoreCase)
			return name.toLowerCase().endsWith(extension.toLowerCase());
		return name.endsWith(extension);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getExtension() {
		return extension;
	}

}
