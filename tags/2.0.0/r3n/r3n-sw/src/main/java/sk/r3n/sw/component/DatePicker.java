package sk.r3n.sw.component;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public final class DatePicker extends JPanel implements UIActionExecutor, MouseListener {

    private class PickerDayField extends JLabel {

        protected int day;

        public PickerDayField(int day) {
            super(Integer.toString(day), JLabel.CENTER);
            this.day = day;
            VarcharField field = new VarcharField(true);
            field.setColumns(3);
            Dimension size = field.getPreferredSize();
            setPreferredSize(size);
            setMinimumSize(size);
            setSelected(false);
        }

        public int getDay() {
            return day;
        }

        public void setSelected(boolean selected) {
            if (selected) {
                setBorder(BorderFactory.createLineBorder(Color.BLUE));
            } else {
                setBorder(BorderFactory.createLineBorder(getBackground()));
            }
        }

    }
    protected DateFormat monthAndYearFormat;

    protected JPanel navigationPanel;

    protected JLabel monthAndYear;

    protected R3NButton previousButton;

    protected R3NButton nextButton;

    protected JPanel form;

    protected Date date;

    protected List<PickerDayField> dayFields;

    public DatePicker(Date date) {
        super();
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.date = calendar.getTime();
        monthAndYearFormat = new SimpleDateFormat("MMMMMMMMM yyyy");
        setLayout(new GridBagLayout());
        navigationPanel = new JPanel(new GridBagLayout());
        previousButton = new R3NButton(R3NAction.PREVIOUS, this);
        navigationPanel.add(previousButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        monthAndYear = new JLabel(monthAndYearFormat.format(date),
                JLabel.CENTER);
        VarcharField field = new VarcharField(true);
        field.setColumns(15);
        monthAndYear.setPreferredSize(field.getPreferredSize());
        monthAndYear.setMinimumSize(field.getPreferredSize());
        monthAndYear.setBorder(BorderFactory.createLoweredBevelBorder());
        navigationPanel.add(monthAndYear, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        nextButton = new R3NButton(R3NAction.NEXT, this);
        navigationPanel.add(nextButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(navigationPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel header = new JPanel(new GridBagLayout());
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.getDefault());
        String[] shortWeekdays = symbols.getShortWeekdays();
        int day = Calendar.SUNDAY;
        for (String string : shortWeekdays) {
            PickerDayField pickerDayField = new PickerDayField(0);
            pickerDayField.setText(string);
            if (pickerDayField.getText().length() == 0) {
                continue;
            }
            int column = getWeekIndex(day, calendar);
            header.add(pickerDayField, new GridBagConstraints(column, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
            if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
                pickerDayField.setForeground(Color.RED);
            }
            day++;
        }
        add(header, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        form = new JPanel(new GridBagLayout());
        Dimension size = new PickerDayField(0).getMinimumSize();
        form.setPreferredSize(new Dimension(size.width * 7 + 7, size.height * 7 + 7));
        add(form, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        createDays(calendar);
    }

    private void createDays(Calendar calendar) {
        monthAndYear.setText(monthAndYearFormat.format(calendar.getTime()));
        if (dayFields != null) {
            for (PickerDayField dayField : dayFields) {
                dayField.removeMouseListener(this);
            }
            form.removeAll();
            dayFields.clear();
        } else {
            dayFields = new ArrayList<>();
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
        int column = getWeekIndex(calendar2.get(Calendar.DAY_OF_WEEK), calendar);
        int row = 0;
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            calendar2.set(Calendar.DAY_OF_MONTH, i);
            PickerDayField pickerDayField = new PickerDayField(i);
            pickerDayField.addMouseListener(this);
            if (calendar2.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                    || calendar2.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                pickerDayField.setForeground(Color.RED);
            }
            form.add(pickerDayField, new GridBagConstraints(column, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
            dayFields.add(pickerDayField);
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
        dayFields.get(calendar.get(Calendar.DAY_OF_MONTH) - 1).setSelected(true);
        revalidate();
        repaint();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            switch ((R3NAction) actionKey) {
                case PREVIOUS:
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    if (month == Calendar.JANUARY) {
                        month = Calendar.DECEMBER;
                        year--;
                    } else {
                        month--;
                    }
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    date = calendar.getTime();
                    createDays(calendar);
                    break;
                case NEXT:
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    if (month == Calendar.DECEMBER) {
                        month = Calendar.JANUARY;
                        year++;
                    } else {
                        month++;
                    }
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    date = calendar.getTime();
                    createDays(calendar);
                    break;
                case SELECT:
                    for (PickerDayField dayField : dayFields) {
                        dayField.setSelected(false);
                    }
                    ((PickerDayField) source).setSelected(true);
                    calendar.set(Calendar.DAY_OF_MONTH, ((PickerDayField) source).getDay());
                    date = calendar.getTime();
                    break;
            }
        }
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    protected int getWeekIndex(int weekDay, Calendar calendar) {
        if (weekDay < calendar.getFirstDayOfWeek()) {
            weekDay += Calendar.SATURDAY;
        }
        return weekDay - calendar.getFirstDayOfWeek();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        execute(R3NAction.SELECT, e.getComponent());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
