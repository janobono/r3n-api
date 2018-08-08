package sk.r3n.sw.test;

import sk.r3n.sw.MessageType;
import sk.r3n.sw.R3NAction;
import sk.r3n.sw.UIActionExecutor;
import sk.r3n.sw.UIActionKey;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel implements UIActionExecutor {

    private final VarcharField titleField;
    private final VarcharField messageField;
    private JComboBox<MessageType> typeBox;

    public MessagePanel() {
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

        messageField = new VarcharField(true);
        add(new JLabel(SWTestBundle.MESSAGE.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        add(messageField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        add(new R3NButton(R3NAction.OK, this), new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    SwingUtil.showMessageDialog(titleField.getValue(), messageField.getText(),
                            (MessageType) typeBox.getSelectedItem());
                    break;
            }
        }
    }

}
