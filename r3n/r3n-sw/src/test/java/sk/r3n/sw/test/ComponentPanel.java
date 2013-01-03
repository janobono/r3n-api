package sk.r3n.sw.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.sw.component.DatePanel;
import sk.r3n.sw.component.MonthPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.BigDecimalField;
import sk.r3n.sw.component.field.DateField;
import sk.r3n.sw.component.field.DirField;
import sk.r3n.sw.component.field.FileField;
import sk.r3n.sw.component.field.IntegerField;
import sk.r3n.sw.component.field.LongField;
import sk.r3n.sw.component.field.MonthField;
import sk.r3n.sw.component.field.ShortField;
import sk.r3n.sw.component.field.TimeField;
import sk.r3n.sw.component.field.TimestampField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.util.SWInputStatusValidator;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class ComponentPanel extends JPanel implements UIActionExecutor {

    private DatePanel datePanel;

    private MonthPanel monthPanel;

    private DateField dateField;

    private MonthField monthField;

    private TimeField timeField;

    private TimestampField timestampField;

    private BigDecimalField bigDecimalField;

    private LongField longField;

    private IntegerField integerField;

    private ShortField shortField;

    private VarcharField varcharField;

    private DirField dirField;

    private FileField fileField;

    public ComponentPanel() {
        super(new GridBagLayout());

        datePanel = new DatePanel(true);
        add(new JLabel(SWTestBundle.DATE.value(), JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(datePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        monthPanel = new MonthPanel(true);
        add(new JLabel(SWTestBundle.MONTH.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(monthPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        dateField = new DateField(true);
        add(new JLabel(SWTestBundle.DATE.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(dateField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        monthField = new MonthField(true);
        add(new JLabel(SWTestBundle.MONTH.value(), JLabel.RIGHT), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(monthField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        timeField = new TimeField(true);
        add(new JLabel(SWTestBundle.TIME.value(), JLabel.RIGHT), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(timeField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        timestampField = new TimestampField(true);
        add(new JLabel(SWTestBundle.TIMESTAMP.value(), JLabel.RIGHT), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(timestampField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        bigDecimalField = new BigDecimalField(true, (short) 2);
        add(new JLabel(SWTestBundle.BIG_DECIMAL.value(), JLabel.RIGHT), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(bigDecimalField, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        longField = new LongField(true);
        add(new JLabel(SWTestBundle.LONG.value(), JLabel.RIGHT), new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(longField, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        integerField = new IntegerField(true);
        add(new JLabel(SWTestBundle.INTEGER.value(), JLabel.RIGHT), new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(integerField, new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        shortField = new ShortField(true);
        add(new JLabel(SWTestBundle.SHORT.value(), JLabel.RIGHT), new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(shortField, new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        varcharField = new VarcharField(true);
        add(new JLabel(SWTestBundle.VARCHAR.value(), JLabel.RIGHT), new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(varcharField, new GridBagConstraints(1, 10, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        dirField = new DirField();
        add(new JLabel(SWTestBundle.DIR.value(), JLabel.RIGHT), new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(dirField, new GridBagConstraints(1, 11, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        fileField = new FileField();
        add(new JLabel(SWTestBundle.FILE.value(), JLabel.RIGHT), new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(fileField, new GridBagConstraints(1, 12, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        add(new R3NButton(R3NAction.OK, this), new GridBagConstraints(0, 13, 2, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    SWInputStatusValidator statusValidator = new SWInputStatusValidator();
                    statusValidator.add(datePanel);
                    statusValidator.add(monthPanel);
                    statusValidator.add(dateField);
                    statusValidator.add(monthField);
                    statusValidator.add(timeField);
                    statusValidator.add(timestampField);
                    statusValidator.add(bigDecimalField);
                    statusValidator.add(longField);
                    statusValidator.add(integerField);
                    statusValidator.add(shortField);
                    statusValidator.add(varcharField);
                    statusValidator.add(dirField);
                    statusValidator.add(fileField);
                    statusValidator.isInputValid();
            }
        }
    }

}
