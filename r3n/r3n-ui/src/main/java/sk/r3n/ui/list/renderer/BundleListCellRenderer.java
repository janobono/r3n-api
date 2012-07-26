package sk.r3n.ui.list.renderer;

import java.util.ResourceBundle;

public class BundleListCellRenderer<T> extends LabelListCellRenderer<T> {

    protected ResourceBundle valueBundle;

    public BundleListCellRenderer(ResourceBundle valueBundle) {
        super();
        this.valueBundle = valueBundle;
    }

    @Override
    public String getText(T value) {
        try {
            String key = value.toString();
            if (valueBundle != null) {
                return valueBundle.getString(key);
            } else {
                return key;
            }
        } catch (Exception e) {
            return "WRONG VALUE DEFINITION!";
        }
    }

    public void setValueBundle(ResourceBundle valueBundle) {
        this.valueBundle = valueBundle;
    }
}
