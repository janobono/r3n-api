package sk.r3n.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BundleResolver {

    public static String resolve(final String baseName, final String key) {
        return ResourceBundle.getBundle(baseName).getString(key);
    }

    public static String resolve(final String baseName, final String key, final Object[] parameters) {
        List<String> messageArguments = new ArrayList<>(parameters.length);
        for (Object obj : parameters) {
            if (obj != null) {
                if (obj instanceof BundleEnum) {
                    messageArguments.add(((BundleEnum) obj).value());
                } else {
                    messageArguments.add(obj.toString());
                }
            } else {
                messageArguments.add("");
            }
        }
        MessageFormat formatter = new MessageFormat("");
        formatter.applyPattern(ResourceBundle.getBundle(baseName).getString(key));
        return formatter.format(messageArguments.toArray());
    }

}
