import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserManagementTest {

    private GeneralUser testUser;

    @BeforeEach
    public void setUp() {
        // Assuming the password is stored as a hashed value,
        // and using some dummy values for other parameters
        testUser = new GeneralUser(
                "testCustomIDKey",
                UserManagement.hashPassword("testPassword"),
                "John Doe",
                "johndoe@example.com",
                "1234567890"
        );
    }

    @Test
    public void testUserCreation() {
        Assertions.assertNotNull(testUser, "User should be created successfully.");
    }

    @Test
    public void testUserIsNotAdmin() {
        Assertions.assertFalse(testUser.isAdmin(), "GeneralUser should not be an admin.");
    }

    // TODO: Add more tests based on methods available in the GeneralUser class
    // and any other requirements you might have.

}
