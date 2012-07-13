package sk.r3n.ui.table.renderer;

public class VarcharTableCellRenderer extends LabelTableCellRenderer<String> {

	private static final long serialVersionUID = 259455297000189578L;

	public VarcharTableCellRenderer() {
		super();
	}

	@Override
	public String getText(String value) {
		return value;
	}

}
