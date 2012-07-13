package sk.r3n.ui.list.renderer;

import javax.swing.UIManager;

public class LookAndFeelRenderer extends LabelListCellRenderer<String> {

	private static final long serialVersionUID = 4604487569113471076L;

	private UIManager.LookAndFeelInfo[] info;

	public LookAndFeelRenderer() {
		super();
		info = UIManager.getInstalledLookAndFeels();
	}

	@Override
	public String getText(String value) {
		for (int i = 0; i < info.length; i++) {
			if (value.equals(info[i].getClassName()))
				return info[i].getName();
		}
		return "WRONG VALUE";
	}

}
