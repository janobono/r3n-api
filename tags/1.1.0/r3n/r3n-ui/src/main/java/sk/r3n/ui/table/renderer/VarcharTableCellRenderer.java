package sk.r3n.ui.table.renderer;

public class VarcharTableCellRenderer extends LabelTableCellRenderer<String> {

    public VarcharTableCellRenderer() {
        super();
    }

    @Override
    public String getText(String value) {
        return value;
    }
}
