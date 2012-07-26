package sk.r3n.ui.list.renderer;

import java.text.SimpleDateFormat;

public class SimpleDateFormatListCellRenderer extends LabelListCellRenderer<SimpleDateFormat> {

    @Override
    public String getText(SimpleDateFormat value) {
        return value.toPattern();
    }
}
