import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class JsonManagementTest {

    private ArrayList<Scroll> originalDBScroll;
    private ArrayList<User> originalDBUser;

    @BeforeEach
    public void setUp() {
        // store all scrolls in the test database in allScrolls
        originalDBScroll = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
        originalDBUser = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
    }

    @AfterEach
    public void tearDown() {
        // restore the original users database
        JSONArray allUsersJson = new JSONArray();
        for (User user : originalDBUser) {
            JSONObject userJson = new JSONObject();
            userJson.put("phone", user.getPhone());
            userJson.put("email", user.getEmailAddress());
            userJson.put("hashedPassword", user.getPassword());
            userJson.put("fullName", user.getFullName());
            userJson.put("customID", user.getCustomIDKey());
            if (user instanceof GeneralUser) {
                userJson.put("type", "general");
            } else if (user instanceof RootAdminUser) {
                userJson.put("type", "rootadmin");
            } else {
                userJson.put("type", "admin");
            }
            allUsersJson.add(userJson);
        }
        JsonManagement.writeToJson("src/test/resources/TestUsers.json", allUsersJson);

        // restore the original scrolls database
        JSONArray allScrollsJson = new JSONArray();
        for (Scroll scroll : originalDBScroll) {
            JSONObject scrollJson = new JSONObject();
            scrollJson.put("scrollContent", scroll.getScrollContent());
            scrollJson.put("scrollUploadDate", scroll.getScrollUploadDate());
            scrollJson.put("scrollPwd", scroll.getScrollPassword());
            scrollJson.put("scrollName", scroll.getScrollName());
            scrollJson.put("scrollUploaderID", scroll.getScrollUploaderID());
            scrollJson.put("noOfDownloads", scroll.getNoOfDownloads());
            scrollJson.put("scrollID", scroll.getScrollID());
            allScrollsJson.add(scrollJson);
        }
        JsonManagement.writeToJson("src/test/resources/TestScrolls.json", allScrollsJson);
    }
    
    // test updateUserProfileJson
    @Test
    public void testUpdateUserProfileJsonPhone() {
        setUp();
        ArrayList<User> allUsers = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        // update the phone number of the first user in the database
        User chosenUser = allUsers.get(0);
        JsonManagement.updateUserProfileJson(chosenUser.getCustomIDKey(), "phone", "04159758588888", "src/test/resources/TestUsers.json");
        // update hashed password of the first user in the database
        String pwd = UserManagement.hashPassword("newPasswordForTest");
        JsonManagement.updateUserProfileJson(chosenUser.getCustomIDKey(), "hashedPassword", pwd, "src/test/resources/TestUsers.json");
        // update full name of the first user in the database
        JsonManagement.updateUserProfileJson(chosenUser.getCustomIDKey(), "fullName", "newFullNameForTest", "src/test/resources/TestUsers.json");
        // update email address of the first user in the database
        JsonManagement.updateUserProfileJson(chosenUser.getCustomIDKey(), "email", "newEmailForTest@gmail.com", "src/test/resources/TestUsers.json");

        // check if the new info is updated
        ArrayList<User> allUsersUpdated = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        for (User user : allUsersUpdated) {
            if (user.getCustomIDKey().equals(chosenUser.getCustomIDKey())) {
                Assertions.assertEquals("04159758588888", user.getPhone());
                Assertions.assertEquals(pwd, user.getPassword());
                Assertions.assertEquals("newFullNameForTest", user.getFullName());
                Assertions.assertEquals("newEmailForTest@gmail.com", user.getEmailAddress());
            }
        }
        // restore the original users database
        tearDown();
    }

    // test getUserJson when there's only one user in the database
    @Test
    public void testGetUserJsonOneUser() {
        setUp();
        ArrayList<User> allUsers = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        // write one user to the database
        User user = allUsers.get(0);
        ArrayList<User> oneUser = new ArrayList<>();
        oneUser.add(user);
        JSONArray oneUserJson = new JSONArray();
        for (User u : oneUser) {
            JSONObject userJson = new JSONObject();
            userJson.put("phone", u.getPhone());
            userJson.put("email", u.getEmailAddress());
            userJson.put("hashedPassword", u.getPassword());
            userJson.put("fullName", u.getFullName());
            userJson.put("customID", u.getCustomIDKey());
            if (u instanceof GeneralUser) {
                userJson.put("type", "general");
            } else if (u instanceof RootAdminUser) {
                userJson.put("type", "rootadmin");
            } else {
                userJson.put("type", "admin");
            }
            oneUserJson.add(userJson);
        }
        JsonManagement.writeToJson("src/test/resources/TestUsers.json", oneUserJson);
        ArrayList<User> allUsersUpdated = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        // check if the user is retrieved
        for (User u : allUsersUpdated) {
            if (u.getCustomIDKey().equals(user.getCustomIDKey())) {
                Assertions.assertEquals(user.getPhone(), u.getPhone());
                Assertions.assertEquals(user.getEmailAddress(), u.getEmailAddress());
                Assertions.assertEquals(user.getPassword(), u.getPassword());
                Assertions.assertEquals(user.getFullName(), u.getFullName());
                Assertions.assertEquals(user.getCustomIDKey(), u.getCustomIDKey());
            }
        }
        // restore the original users database
        tearDown();

    }

    // test write a user to json file
    @Test
    public void testWriteUserToJSON() {
        setUp();
        GeneralUser gu = new GeneralUser("510430520", "111111111", "testFullName", "testemail@gmail.com", "11111111111123123");
        AdminUser au = new AdminUser("410430520", "111111111", "testFullName", "emailemail", "111111111111343123");
        RootAdminUser rau = new RootAdminUser("310430520", "111111111", "testFullName", "emailemail", "111111211111343123");

        JsonManagement.writeNewUserJson("src/test/resources/TestUsers.json", gu);
        JsonManagement.writeNewUserJson("src/test/resources/TestUsers.json", au);
        JsonManagement.writeNewUserJson("src/test/resources/TestUsers.json", rau);

        ArrayList<User> allUsers = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        // check if the user is retrieved
        Assertions.assertEquals(originalDBUser.size() + 3, allUsers.size());
        // restore the original users database
        tearDown();
    }
}
