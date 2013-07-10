package sk.r3n.ui;

import java.util.ResourceBundle;
import static org.junit.Assert.*;
import org.junit.Test;

public class BaseUITest {

    @Test
    public void fileFilterTest() {
        R3NFileFilter fileFilter = new AllFileFilter();
        assertEquals(
                ResourceBundle.getBundle(AllFileFilter.class.getCanonicalName()).getString(AllFileFilter.DESCRIPTION),
                fileFilter.getDescription());
    }

    @Test
    public void messageTypeTest() {
        for (MessageType messageType : MessageType.values()) {
            assertNotNull(messageType.value());
        }
    }

    @Test
    public void actionTest() {
        for (R3NAction action : R3NAction.values()) {
            assertNotNull(action.actionName());
        }
    }
}
