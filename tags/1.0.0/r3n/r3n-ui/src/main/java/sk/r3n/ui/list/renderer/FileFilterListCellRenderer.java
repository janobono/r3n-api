package sk.r3n.ui.list.renderer;

import sk.r3n.ui.R3NFileFilter;

public class FileFilterListCellRenderer extends
		LabelListCellRenderer<R3NFileFilter> {

	private static final long serialVersionUID = -6434199185548561772L;

	@Override
	public String getText(R3NFileFilter value) {
		return value.getDescription();
	}

}
