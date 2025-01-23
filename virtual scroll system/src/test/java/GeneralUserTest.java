import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralUserTest {

    @Test
    public void testGeneralUserConstructor() {
        String customIDKey = "john_doe";
        String password = "password123";
        String fullName = "John Doe";
        String emailAddress = "john@example.com";
        String phone = "1234567890";

        GeneralUser user = new GeneralUser(customIDKey, password, fullName, emailAddress, phone);

        // Verify that the user object is not null
        assertNotNull(user);

        // Verify that the user's properties are correctly set
        assertEquals(customIDKey, user.getCustomIDKey());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals(emailAddress, user.getEmailAddress());
        assertEquals(phone, user.getPhone());
    }

    @Test
    public void testIsAdmin() {
        GeneralUser user = new GeneralUser("john_doe", "password123", "John Doe", "john@example.com", "1234567890");

        // Verify that the user is not an admin
        assertFalse(user.isAdmin());
    }
}
