import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuestUserTest {

    @Test
    public void testGuestUserConstructor() {
        GuestUser guestUser = new GuestUser();

        // Verify that the guestUser object is not null
        assertNotNull(guestUser);

        // Verify that the properties of the guestUser are correctly set (empty strings)
        assertEquals("", guestUser.getCustomIDKey());
        assertEquals("", guestUser.getPassword());
        assertEquals("", guestUser.getFullName());
        assertEquals("", guestUser.getEmailAddress());
        assertEquals("", guestUser.getPhone());
    }

    @Test
    public void testIsAdmin() {
        GuestUser guestUser = new GuestUser();

        // Verify that the guestUser is not an admin
        assertFalse(guestUser.isAdmin());
    }
}
