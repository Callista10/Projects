import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ScrollTest {
    private static Scroll scroll1;

    @BeforeEach
    public void setUp() {
        scroll1 = new Scroll("Scroll1", "Uploader1", "010101", "password1");
    }

    @Test
    public void testGetScrollID() {
        Assertions.assertNotNull(scroll1.getScrollID());
    }

    @Test
    public void testGetScrollName() {
        Assertions.assertEquals("Scroll1", scroll1.getScrollName());
    }

    @Test
    public void testGetScrollUploaderID() {
        Assertions.assertEquals("Uploader1", scroll1.getScrollUploaderID());
    }

    @Test
    public void testGetScrollUploadDate() {
        Assertions.assertNotNull(scroll1.getScrollUploadDate());
    }

    @Test
    public void testGetNoOfDownloads() {
        Assertions.assertEquals(0, scroll1.getNoOfDownloads());
    }

    @Test
    public void testSetScrollContentValid() {
        Assertions.assertEquals("010101", scroll1.getScrollContent());
    }

    @Test
    public void testGetScrollPassword() {
        Assertions.assertEquals("password1", scroll1.getScrollPassword());
    }

    @Test
    public void testSetNoOfDownloads() {
        int initialDownloads = scroll1.getNoOfDownloads();
        scroll1.setNoOfDownloads();
        Assertions.assertEquals(initialDownloads + 1, scroll1.getNoOfDownloads());
    }

}
