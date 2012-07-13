package sk.r3n.ui.table.renderer;

import java.util.ResourceBundle;

public class BundleTableCellRenderer<T> extends LabelTableCellRenderer<T> {

	private static final long serialVersionUID = 4022394375559725855L;

	protected ResourceBundle valueBundle;

	public BundleTableCellRenderer(ResourceBundle valueBundle) {
		super();
		this.valueBundle = valueBundle;
	}

	@Override
	public String getText(T value) {
		try {
			String key = value.toString();
			if (valueBundle != null)
				return valueBundle.getString(key);
			else
				return key;
		} catch (Exception e) {
			return "WRONG VALUE DEFINITION!";
		}
	}

	public void setValueBundle(ResourceBundle valueBundle) {
		this.valueBundle = valueBundle;
	}

}
