package sk.r3n.ui.list.renderer;

import java.util.ResourceBundle;

public class BundleListCellRenderer<T> extends LabelListCellRenderer<T> {

	private static final long serialVersionUID = -7799314006804968746L;

	protected ResourceBundle valueBundle;

	public BundleListCellRenderer(ResourceBundle valueBundle) {
		super();
		this.valueBundle = valueBundle;
	}

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
