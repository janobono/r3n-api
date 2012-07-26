package sk.r3n.ui.table.renderer;

public class IntegerTableCellRenderer extends LabelTableCellRenderer<Integer> {

    public IntegerTableCellRenderer() {
        super();
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public String getText(Integer value) {
        return value.toString();
    }
}
