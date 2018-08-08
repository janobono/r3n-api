package sk.r3n.sw.component;

import sk.r3n.sw.R3NAction;
import sk.r3n.sw.UIActionExecutor;
import sk.r3n.sw.UIActionKey;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.util.UIActionListener;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class MonthPicker extends JPanel implements UIActionExecutor {

    public JComboBox<Integer> monthBox;

    public JComboBox<Integer> yearBox;

    private boolean blocked;

    public MonthPicker(Date date) {
        super(new GridBagLayout());
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // MONTH
        monthBox = new JComboBox<>();
        monthBox.setRenderer(new R3NListCellRenderer<Integer>() {
            @Override
            public String getText(Integer value) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, value);
                return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            }

        });
        for (int i = 0; i < 12; i++) {
            monthBox.addItem(i);
        }
        monthBox.setSelectedItem(calendar.get(Calendar.MONTH));
        add(monthBox, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                0, 0, 0), 0, 0));
        // YEAR
        yearBox = new JComboBox<>();
        yearBox.addActionListener(new UIActionListener(R3NAction.SELECT, this));
        setYear(calendar.get(Calendar.YEAR));
        add(yearBox, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
                0, 0, 0), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case SELECT:
                    if (!blocked) {
                        setYear((int) yearBox.getSelectedItem());
                    }
                    break;
            }
        }
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, (int) yearBox.getSelectedItem());
        calendar.set(Calendar.MONTH, (int) monthBox.getSelectedItem());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void setYear(int year) {
        blocked = true;
        int min = year - 20;
        int max = year + 20;
        yearBox.removeAllItems();
        for (int i = min; i <= max; i++) {
            yearBox.addItem(i);
        }
        yearBox.setSelectedItem(year);
        blocked = false;
    }

}
