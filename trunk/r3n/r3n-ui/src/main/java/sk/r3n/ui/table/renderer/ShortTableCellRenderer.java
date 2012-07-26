package sk.r3n.ui.table.renderer;

public class ShortTableCellRenderer extends LabelTableCellRenderer<Short> {

    public ShortTableCellRenderer() {
        super();
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public String getText(Short value) {
        return value.toString();
    }
}
