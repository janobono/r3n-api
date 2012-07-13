package sk.r3n.ui;

import java.awt.Color;
import java.net.URL;

public interface StartupService {

	public void autoIncrementProgress();

	public void finishProgress();

	public void hideService();
	
	public void incrementProgress();

	public boolean isServiceVisible();

	public void setAppIcon(URL url);

	public void setAppImage(URL url, boolean stretch);

	public void setAppName(String appName);

	public void setInfoText(String infoText);
	
	public void setInfoTextForegroun(Color infoTextColor);

	public void showService(int width, int height);

	public void startProgress();

}
