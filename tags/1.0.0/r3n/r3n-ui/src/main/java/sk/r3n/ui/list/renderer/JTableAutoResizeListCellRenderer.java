package sk.r3n.ui.list.renderer;

import java.util.ResourceBundle;

public class JTableAutoResizeListCellRenderer extends
		LabelListCellRenderer<Integer> {

	private static final long serialVersionUID = -2905805632318333446L;

	private ResourceBundle resourceBundle;

	public JTableAutoResizeListCellRenderer() {
		super();
		resourceBundle = ResourceBundle.getBundle(this.getClass()
				.getCanonicalName());
	}

	@Override
	public String getText(Integer value) {
		return resourceBundle.getString(value.toString());
	}

}
