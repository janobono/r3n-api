package sk.r3n.sw.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.DirField;
import sk.r3n.sw.component.field.ValueField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.AllFileFilter;
import sk.r3n.ui.ExtensionFileFilter;
import sk.r3n.ui.Filter;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.R3NFileFilter;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class FileOpenPanel extends JPanel implements UIActionExecutor {

    private JComboBox<Filter> filterBox;

    private DirField dirField;

    private VarcharField titleField;

    private VarcharField fileNameField;

    private ValueField<File> resultField;

    public FileOpenPanel() {
        super(new GridBagLayout());

        filterBox = new JComboBox<>();
        filterBox.setRenderer(new R3NListCellRenderer<Filter>() {
            @Override
            public String getText(Filter value) {
                return value.name();
            }
        });
        for (Filter filter : Filter.values()) {
            filterBox.addItem(filter);
        }
        add(new JLabel(SWTestBundle.FILTER.value(), JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(filterBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        dirField = new DirField();
        dirField.setValue(System.getProperty("user.home"));
        add(new JLabel(SWTestBundle.DEFAULT_DIRECTORY.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(dirField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        titleField = new VarcharField(true);
        add(new JLabel(SWTestBundle.TITLE.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(titleField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        fileNameField = new VarcharField(true);
        add(new JLabel(SWTestBundle.FILE_NAME.value(), JLabel.RIGHT), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(fileNameField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        resultField = new ValueField<>();
        add(new JLabel(SWTestBundle.RESULT.value(), JLabel.RIGHT), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(resultField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        add(new R3NButton(R3NAction.OK, this), new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    File file = SwingUtil.openFile(
                            (Filter) filterBox.getSelectedItem(),
                            titleField.getValue(),
                            new File(dirField.getValue()),
                            new R3NFileFilter[]{new AllFileFilter(),
                        new ExtensionFileFilter(new String[]{".txt"}, "Text files [*.txt]")},
                            fileNameField.getValue());
                    resultField.setValue(file);
                    break;
            }
        }
    }
}
