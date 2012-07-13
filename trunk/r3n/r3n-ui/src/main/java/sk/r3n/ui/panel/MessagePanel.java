package sk.r3n.ui.panel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MessagePanel extends JPanel {

	private static final long serialVersionUID = 3258125877656040247L;
	
	private static final String NEW_LINE = "\r\n";

	public MessagePanel() {
		super();
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setLayout(new GridLayout(0, 1, 0, 2));
	}

	private void addMessage(Object mess) {
		if (mess instanceof Component) {
			this.add((Component) mess);
		} else if (mess instanceof Icon) {
			JLabel label = new JLabel((Icon) mess);
			label.setHorizontalAlignment(JLabel.LEFT);
			this.add(label);
		} else {
			JLabel lbl = new JLabel(mess.toString());
			lbl.setHorizontalAlignment(JLabel.LEFT);
			this.add(lbl);
		}
	}

	private void parseMessage(Object message) {
		if (message instanceof String) {
			StringTokenizer st = new StringTokenizer((String) message, NEW_LINE);
			while (st.hasMoreTokens()) {
				String stMess = st.nextToken();
				addMessage(stMess);
			}
		} else
			addMessage(message);
	}

	public void setMessage(Object message) {
		if (message instanceof Object[]) {
			for (int i = 0; i < ((Object[]) message).length; i++)
				parseMessage(((Object[]) message)[i]);
		} else
			parseMessage(message);
	}

}