package sk.r3n.ui.table.renderer;

import java.math.BigDecimal;

import sk.r3n.ui.component.field.BigDecimalField;

public class BigDecimalTableCellRenderer extends
		LabelTableCellRenderer<BigDecimal> {

	private static final long serialVersionUID = 2898923252535744910L;

	private short scale;
	private char separator;

	public BigDecimalTableCellRenderer(short scale, char separator) {
		super();
		this.scale = scale;
		this.separator = separator;
		setHorizontalAlignment(RIGHT);
	}

	@Override
	public String getText(BigDecimal value) {
		return BigDecimalField.toString(value, scale, separator);
	}

}
