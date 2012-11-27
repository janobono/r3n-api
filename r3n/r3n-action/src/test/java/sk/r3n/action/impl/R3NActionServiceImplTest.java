package sk.r3n.action.impl;

import static org.junit.Assert.*;
import org.junit.Test;
import sk.r3n.action.R3NActionService;

public class R3NActionServiceImplTest {

    private R3NActionService impl;

    @Test
    public void actionServiceTest() {
        impl = new R3NActionServiceImpl();
        impl.add("test", 100);
        impl.setProperty("test", 100, "key", "value");
        assertFalse(impl.isAction("y", 100));
        assertTrue(impl.isAction("test", 100));
        assertNull(impl.getProperty("test", 100, "k"));
        assertEquals("value", impl.getProperty("test", 100, "key"));
        assertEquals(1, impl.getGroups().size());
        assertEquals(1, impl.getActions("test").size());
        assertEquals(impl.toString("test", 100), impl.getActions("test").get(0));
    }

}
