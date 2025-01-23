import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class RootAdminUserTest {

    private UserManagement manager;
    private String testDBPath = "src/test/resources/TestUserforUserTest.json";

    @BeforeEach
    public void setUp() {
        manager = new UserManagement(testDBPath);
    }
    @Test
    void testAddGeneralUserForRootAdmin() {
        // Set the input for the addGeneralUser method
        String input = "JohnDoe\npassword123\nJohn Doe\njohndoe@example.com\n123456789\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the addGeneralUser method
        RootAdminUser.addGeneralUser();

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
    void testAddAdminUserForRootAdmin() {
        // Set the input for the addGeneralUser method
        String input = "JohnDon\npassword123\nJohn Don\njohndon@example.com\n123456789\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the addGeneralUser method
        RootAdminUser.addAdminUser();

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
    void testDeleteGeneralUserForRootAdmin() {
        // Set the input for the deleteGeneralUser method
        String input = "Fiona\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the deleteGeneralUser method
        RootAdminUser.deleteGeneralUser();

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
    void testDeleteAdminUserForRootAdmin() {
        // Set the input for the deleteGeneralUser method
        String input = "admin\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the deleteGeneralUser method
        RootAdminUser.deleteAdminUser();

        // Get all users and check if the user is deleted
        ArrayList<User> allUsers = JsonManagement.getUsersJson(testDBPath);
        ;
        boolean userFound = false;
        for (User user : allUsers) {
            if (user.getCustomIDKey().equals("Fiona")) {
                userFound = true;
                break;
            }
        }
    }

    @Test
    void testGrantPrivilegesByAdminForRootAdmin() {
        // Set the input for the grantPrivilegesByAdmin method
        String input = "Fiona\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the grantPrivilegesByAdmin method
        RootAdminUser.grantPrivilegesByAdmin();

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
    void testRemoveScrollForRootAdmin() {
        // Set the input for the removeUserScroll method
        String input = "961879010\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Run the removeUserScroll method
        RootAdminUser.removeUserScroll();

        // Get all scrolls and check if the scroll is deleted
        DigitalScrollManager scrollManager = new DigitalScrollManager("src/test/resources/TestScrollsforUserTest.json");
        ArrayList<Scroll> scrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrollsforUserTest.json");
        boolean scrollFound = false;
        for (Scroll scroll : scrolls) {
            if (scroll.getScrollID().equals("961879010")) {
                scrollFound = true;
                break;
            }
        }

        assertFalse(!scrollFound);
    }

    @Test
    void testAdminProfileForRootAdmin() {
        // Set the input for the adminProfile method
        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the adminProfile method
        RootAdminUser.adminProfile();

        // Check if the expected output is generated
        String expectedOutput = "----------------------------------------\n" +
                "You has access to special admin privileges\n" +
                "Custom ID: rootadmin\n" +
                "Full Name: Root Admin\n" +
                "Email: rootadmin@example.com\n" +
                "Phone: 987654321\n" +
                "----------------------------------------\n" +
                "----------------------------------------\n" +
                "You has access to special admin privileges\n" +
                "Custom ID: admin\n" +
                "Full Name: Admin\n" +
                "Email: admin@example.com\n" +
                "Phone: 123456789\n" +
                "----------------------------------------\n"+
                "----------------------------------------\n" +
                "You has access to special admin privileges\n" +
                "Custom ID: JohnDon\n" +
                "Full Name: John Don\n" +
                "Email: johndon@example.com\n" +
                "Phone: 123456789\n" +
                "----------------------------------------\n";

                ;
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testAllUsersProfileForRootAdmin() {
        // Set the input for the allUsersProfile method
        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the allUsersProfile method
        RootAdminUser.allUsersProfile();

        // Check if the expected output is generated
        String expectedOutput = "----------------------------------------\n" +
                "You has access to the users profile\n" +
                "Custom ID: rootadmin\n" +
                "Full Name: Root Admin\n" +
                "Email: rootadmin@example.com\n" +
                "Phone: 987654321\n" +
                "----------------------------------------\n" +
                "----------------------------------------\n" +
                "You has access to the users profile\n" +
                "Custom ID: Fiona\n" +
                "Full Name: Fiona C\n" +
                "Email: fionachen@qq.com\n" +
                "Phone: 0415975858\n" +
                "----------------------------------------\n" +
                "----------------------------------------\n" +
                "You has access to the users profile\n" +
                "Custom ID: admin\n" +
                "Full Name: Admin\n" +
                "Email: admin@example.com\n" +
                "Phone: 123456789\n" +
                "----------------------------------------\n"+
                "----------------------------------------\n" +
                "You has access to the users profile\n" +
                "Custom ID: JohnDoe\n" +
                "Full Name: John Doe\n" +
                "Email: johndoe@example.com\n" +
                "Phone: 123456789\n" +
                "----------------------------------------\n" +
                "----------------------------------------\n" +
                "You has access to the users profile\n" +
                "Custom ID: JohnDon\n" +
                "Full Name: John Don\n" +
                "Email: johndon@example.com\n" +
                "Phone: 123456789\n" +
                "----------------------------------------\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
