package sk.r3n.ui.component;

import java.util.ResourceBundle;

import sk.r3n.ui.list.renderer.LabelListCellRenderer;

public class BundleComboBox<T> extends R3NComboBox<T> {

	private static final long serialVersionUID = -1171138785647592253L;

	public class BundleRenderer extends LabelListCellRenderer<T> {

		private static final long serialVersionUID = 1509163518786644292L;

		@Override
		public String getText(T value) {
			try {
				String key = value.toString();
				if (valueBundle != null)
					return valueBundle.getString(key);
				else
					return key;
			} catch (Exception e) {
				return wrongValueText;
			}
		}
	}

	private ResourceBundle valueBundle;

	private String wrongValueText;

	public BundleComboBox(ResourceBundle valueBundle, String wrongValueText) {
		super();
		this.valueBundle = valueBundle;
		this.wrongValueText = wrongValueText;
		setRenderer(new BundleRenderer());
	}

	@SuppressWarnings("unchecked")
	public T getSelectedItemValue() {
		if (getSelectedItem() != null)
			return (T) getSelectedItem();
		return null;
	}

	public void setValueBundle(ResourceBundle valueBundle) {
		this.valueBundle = valueBundle;
	}

}