package sk.r3n.ui.table.renderer;

public class ShortTableCellRenderer extends LabelTableCellRenderer<Short> {

	private static final long serialVersionUID = 5184035169820986369L;

	public ShortTableCellRenderer() {
		super();
		setHorizontalAlignment(RIGHT);
	}

	@Override
	public String getText(Short value) {
		return value.toString();
	}

}
