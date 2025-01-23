import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;

public class DigitalScrollManagerTest {
    private DigitalScrollManager manager;
    private String testDBPath = "src/test/resources/TestScrolls.json";
    private Scroll sampleScroll;

    @BeforeEach
    public void setUp() {
        manager = new DigitalScrollManager(testDBPath);
        ArrayList<Scroll> scrolls = manager.getAllScrolls();
        if (!scrolls.isEmpty()) {
            sampleScroll = scrolls.get(0);
        }
    }

    // @Test
    // public void testGetAllScrolls() {
    //     ArrayList<Scroll> scrolls = manager.getAllScrolls();
    //     Assertions.assertNotNull(scrolls);
    //     Assertions.assertEquals(2, scrolls.size());
    // }

    @Test
    public void testAddScroll() {
        Scroll newScroll = new Scroll("Scroll1", "admin", "010101", "password1");
        manager.addScroll(newScroll);
        ArrayList<Scroll> scrolls = manager.getAllScrolls();
        Assertions.assertTrue(scrolls.contains(newScroll));
    }

    @Test
    public void testEditScrollContent() {
        if (sampleScroll != null) {
            String modifiedContent = "001100";
            manager.updateScrollContent(sampleScroll, modifiedContent);
            ArrayList<Scroll> scrolls = manager.getAllScrolls();

            Scroll updatedScroll = null;
            for (Scroll scroll : scrolls) {
                if (scroll.getScrollID().equals(sampleScroll.getScrollID())) {
                    updatedScroll = scroll;
                    break;
                }
            }

            Assertions.assertNotNull(updatedScroll);
            Assertions.assertEquals(modifiedContent, updatedScroll.getScrollContent());
        } else {
            Assertions.fail("No existing scrolls available for testing.");
        }
    }

    @Test
    public void testRemoveScroll() {
        if (sampleScroll != null) {
            manager.removeScroll(sampleScroll);
            ArrayList<Scroll> scrolls = manager.getAllScrolls();
            Assertions.assertFalse(scrolls.contains(sampleScroll));
        } else {
            Assertions.fail("No existing scrolls available for testing.");
        }
    }
}
