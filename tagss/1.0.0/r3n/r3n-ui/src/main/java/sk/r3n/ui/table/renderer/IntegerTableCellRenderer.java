package sk.r3n.ui.table.renderer;

public class IntegerTableCellRenderer extends LabelTableCellRenderer<Integer> {

	private static final long serialVersionUID = -3954764495809650475L;

	public IntegerTableCellRenderer() {
		super();
		setHorizontalAlignment(RIGHT);
	}

	@Override
	public String getText(Integer value) {
		return value.toString();
	}

}
