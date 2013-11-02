package sk.r3n.sw.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.ValueField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.Answer;
import sk.r3n.ui.MessageType;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class QuestionPanel extends JPanel implements UIActionExecutor {

    private JComboBox<MessageType> typeBox;

    private final VarcharField titleField;

    private final VarcharField questionField;

    private final JCheckBox yesNoCancelBox;

    private final ValueField<File> resultField;

    public QuestionPanel() {
        super(new GridBagLayout());

        typeBox = new JComboBox<>();
        typeBox.setRenderer(new R3NListCellRenderer<MessageType>() {
            @Override
            public String getText(MessageType value) {
                return value.value();
            }

        });
        for (MessageType messageType : MessageType.values()) {
            typeBox.addItem(messageType);
        }
        add(new JLabel(SWTestBundle.TYPE.value(), JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(typeBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        titleField = new VarcharField(true);
        add(new JLabel(SWTestBundle.TITLE.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(titleField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        questionField = new VarcharField(true);
        add(new JLabel(SWTestBundle.QUESTION.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(questionField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        yesNoCancelBox = new JCheckBox(SWTestBundle.YES_NO_CANCEL.value());
        add(yesNoCancelBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        resultField = new ValueField<>();
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
                    Answer answer;
                    if (yesNoCancelBox.isSelected()) {
                        answer = SwingUtil.showYesNoCancelDialog(titleField.getValue(), questionField.getText(),
                                (MessageType) typeBox.getSelectedItem());
                    } else {
                        answer = SwingUtil.showYesNoDialog(titleField.getValue(), questionField.getText(),
                                (MessageType) typeBox.getSelectedItem());
                    }
                    resultField.setText(answer.name());
                    break;
            }
        }
    }

}
