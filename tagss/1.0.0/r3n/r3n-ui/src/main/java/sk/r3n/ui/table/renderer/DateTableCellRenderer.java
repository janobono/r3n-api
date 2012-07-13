package sk.r3n.ui.table.renderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTableCellRenderer extends LabelTableCellRenderer<Date> {

	private static final long serialVersionUID = 7517467729379682240L;

	private DateFormat dateFormat;

	public DateTableCellRenderer() {
		super();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public String getText(Date value) {
		return dateFormat.format(value);
	}

}
