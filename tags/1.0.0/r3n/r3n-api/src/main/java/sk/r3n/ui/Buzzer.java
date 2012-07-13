package sk.r3n.ui;

import java.awt.Component;

public interface Buzzer {

	public void buzz(Component source);

	public void buzz(Component source, String text);

}
