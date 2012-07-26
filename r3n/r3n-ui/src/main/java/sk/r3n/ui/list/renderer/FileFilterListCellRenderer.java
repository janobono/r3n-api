package sk.r3n.ui.list.renderer;

import sk.r3n.ui.R3NFileFilter;

public class FileFilterListCellRenderer extends LabelListCellRenderer<R3NFileFilter> {

    @Override
    public String getText(R3NFileFilter value) {
        return value.getDescription();
    }
}
