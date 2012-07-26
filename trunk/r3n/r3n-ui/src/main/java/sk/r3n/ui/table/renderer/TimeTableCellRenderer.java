package sk.r3n.ui.table.renderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTableCellRenderer extends LabelTableCellRenderer<Date> {

    private DateFormat dateFormat;

    public TimeTableCellRenderer() {
        super();
        dateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String getText(Date value) {
        return dateFormat.format(value);
    }
}
