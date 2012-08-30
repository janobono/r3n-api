package sk.r3n.ui.list.renderer;

import java.util.ResourceBundle;

public class BooleanListCellRenderer extends LabelListCellRenderer<Boolean> {

    private ResourceBundle resourceBundle;

    public BooleanListCellRenderer() {
        super();
        resourceBundle = ResourceBundle.getBundle(this.getClass().getCanonicalName());
    }

    @Override
    public String getText(Boolean value) {
        return resourceBundle.getString(value.toString());
    }
}
