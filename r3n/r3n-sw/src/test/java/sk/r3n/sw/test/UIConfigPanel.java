package sk.r3n.sw.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class UIConfigPanel extends JPanel implements UIActionExecutor, ChangeListener {
    
    private JComboBox<Locale> localeBox;
    
    private JComboBox<String> lookAndFeelBox;
    
    private JLabel infoLabel;
    
    public UIConfigPanel() {
        super(new GridBagLayout());
        
        localeBox = new JComboBox<>();
        add(new JLabel(SWTestBundle.LOCALE.value(), JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(localeBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        
        lookAndFeelBox = new JComboBox<>();
        add(new JLabel(SWTestBundle.LOOK_AND_FEEL.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(lookAndFeelBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        
        add(new R3NButton(R3NAction.OK, this), new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        
        infoLabel = new JLabel();
        add(infoLabel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));
    }
    
    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    Locale.setDefault((Locale) localeBox.getSelectedItem());
                    
                    try {
                        UIManager.setLookAndFeel((String) lookAndFeelBox.getSelectedItem());
                    } catch (Exception e) {
                    }
                    
                    infoLabel.setText(SWTestBundle.CONFIG_INFO.value());
                    break;
            }
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        localeBox.removeAllItems();
        for (Locale locale : Locale.getAvailableLocales()) {
            localeBox.addItem(locale);
        }
        localeBox.setSelectedItem(Locale.getDefault());
        
        lookAndFeelBox.removeAllItems();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            lookAndFeelBox.addItem(info.getClassName());
        }
        lookAndFeelBox.setSelectedItem(UIManager.getLookAndFeel().getClass().getCanonicalName());
    }
    
}
