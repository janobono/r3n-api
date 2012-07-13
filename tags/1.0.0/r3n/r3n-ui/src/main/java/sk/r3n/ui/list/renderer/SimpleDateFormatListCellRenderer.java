package sk.r3n.ui.list.renderer;

import java.text.SimpleDateFormat;

public class SimpleDateFormatListCellRenderer extends
		LabelListCellRenderer<SimpleDateFormat> {

	private static final long serialVersionUID = -6275676373986843449L;

	@Override
	public String getText(SimpleDateFormat value) {
		return value.toPattern();
	}

}
