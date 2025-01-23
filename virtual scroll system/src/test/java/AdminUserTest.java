import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class AdminUserTest {

    private UserManagement manager;
    private String testDBPath = "src/test/resources/TestUserforUserTest.json";
    private DigitalScrollManager manager2;
    private String testDBPath2 = "src/test/resources/TestScrollsforUserTest.json";


    @BeforeEach
    public void setUp() {
        manager = new UserManagement(testDBPath);
        manager2 = new DigitalScrollManager(testDBPath2);

    }
    @Test
    void testAddGeneralUser() {
        // Set the input for the addGeneralUser method
        String input = "JohnDoe\npassword123\nJohn Doe\njohndoe@example.com\n123456789\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the addGeneralUser method
        AdminUser.addGeneralUser();

        // Get all users and check if the new user is added
        ArrayList<User> allUsers = JsonManagement.getUsersJson(testDBPath);;
        boolean userFound = false;
        for (User user : allUsers) {
            if (user.getCustomIDKey().equals("JohnDoe")) {
                userFound = true;
                break;
            }
        }

        assertTrue(userFound);
    }

    @Test
    void testAddAdminUser() {
        // Set the input for the addGeneralUser method
        String input = "JohnDon\npassword123\nJohn Don\njohndon@example.com\n123456789\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the addGeneralUser method
        AdminUser.addAdminUser();

        // Get all users and check if the new user is added
        ArrayList<User> allUsers = JsonManagement.getUsersJson(testDBPath);;
        boolean userFound = false;
        for (User user : allUsers) {
            if (user.getCustomIDKey().equals("JohnDon")) {
                userFound = true;
                break;
            }
        }

        assertTrue(userFound);
    }
    @Test
    void testDeleteGeneralUser() {
        // Set the input for the deleteGeneralUser method
        String input = "Fiona\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the deleteGeneralUser method
        AdminUser.deleteGeneralUser();

        // Get all users and check if the user is deleted
        ArrayList<User> allUsers = JsonManagement.getUsersJson(testDBPath);;
        boolean userFound = false;
        for (User user : allUsers) {
            if (user.getCustomIDKey().equals("Fiona")) {
                userFound = true;
                break;
            }
        }

        assertFalse(!userFound);
    }

    @Test
    void testGrantPrivilegesByAdmin() {
        // Set the input for the grantPrivilegesByAdmin method
        String input = "Fiona\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the grantPrivilegesByAdmin method
        AdminUser.grantPrivilegesByAdmin();

        // Get all users and check if the user's privileges are updated
        ArrayList<User> allUsers = JsonManagement.getUsersJson(testDBPath);;
        boolean isAdmin = false;
        for (User user : allUsers) {
            if (user.getCustomIDKey().equals("Fiona") && user instanceof AdminUser) {
                isAdmin = true;
                break;
            }
        }

        assertTrue(!isAdmin);
    }

    @Test
    void testRemoveUserScroll() {
        // Set the input for the removeUserScroll method
        String input = "961879011\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the removeUserScroll method
        AdminUser.removeUserScroll();

        // Get all scrolls and check if the scroll is deleted
        DigitalScrollManager scrollManager = new DigitalScrollManager("src/test/resources/TestScrollsforUserTest.json");
        ArrayList<Scroll> scrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrollsforUserTest.json");
        boolean scrollFound = false;
        for (Scroll scroll : scrolls) {
            if (scroll.getScrollID().equals("961879011")) {
                scrollFound = true;
                break;
            }
        }

        assertFalse(!scrollFound);
    }

    @Test
    void testAdminProfile() {
        // Set the input for the adminProfile method
        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the adminProfile method
        AdminUser.adminProfile();

        // Check if the expected output is generated
        String expectedOutput = """
        ----------------------------------------
        You has access to special admin privileges
        Custom ID: rootadmin
        Full Name: Root Admin
        Email: rootadmin@example.com
        Phone: 987654321
        ----------------------------------------
        ----------------------------------------
        You has access to special admin privileges
        Custom ID: admin
        Full Name: Admin
        Email: admin@example.com
        Phone: 123456789
        ----------------------------------------
        ----------------------------------------
        You has access to special admin privileges
        Custom ID: JohnDon
        Full Name: John Don
        Email: johndon@example.com
        Phone: 123456789
        ----------------------------------------
        """;
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testAllUsersProfile() {
        // Set the input for the allUsersProfile method
        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the allUsersProfile method
        AdminUser.allUsersProfile();

        // Check if the expected output is generated
        String expectedOutput = """
        ----------------------------------------
        You has access to the users profile
        Custom ID: rootadmin
        Full Name: Root Admin
        Email: rootadmin@example.com
        Phone: 987654321
        ----------------------------------------
        ----------------------------------------
        You has access to the users profile
        Custom ID: Fiona
        Full Name: Fiona C
        Email: fionachen@qq.com
        Phone: 0415975858
        ----------------------------------------
        ----------------------------------------
        You has access to the users profile
        Custom ID: admin
        Full Name: Admin
        Email: admin@example.com
        Phone: 123456789
        ----------------------------------------
        ----------------------------------------
        You has access to the users profile
        Custom ID: JohnDoe
        Full Name: John Doe
        Email: johndoe@example.com
        Phone: 123456789
        ----------------------------------------
        ----------------------------------------
        You has access to the users profile
        Custom ID: JohnDon
        Full Name: John Don
        Email: johndon@example.com
        Phone: 123456789
        ----------------------------------------
        """;
        assertEquals(expectedOutput, outContent.toString());
    }
}
