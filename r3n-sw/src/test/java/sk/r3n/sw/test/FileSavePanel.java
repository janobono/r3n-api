package sk.r3n.sw.test;

import sk.r3n.sw.*;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.DirField;
import sk.r3n.sw.component.field.ValueField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileSavePanel extends JPanel implements UIActionExecutor {

    private final DirField dirField;
    private final VarcharField titleField;
    private final VarcharField fileNameField;
    private final ValueField<File> resultField;
    private JComboBox<Filter> filterBox;

    public FileSavePanel() {
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
                    File file = SwingUtil.saveFile(
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
