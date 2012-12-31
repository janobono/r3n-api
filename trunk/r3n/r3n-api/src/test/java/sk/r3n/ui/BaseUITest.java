package sk.r3n.ui;

import static org.junit.Assert.*;
import org.junit.Test;
import sk.r3n.util.BundleResolver;

public class BaseUITest {

    @Test
    public void fileFilterTest() {
        R3NFileFilter fileFilter = new AllFileFilter();
        assertEquals(BundleResolver.resolve(AllFileFilter.class.getCanonicalName(), AllFileFilter.DESCRIPTION),
                fileFilter.getDescription());
        assertNull(fileFilter.getExtension());
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
