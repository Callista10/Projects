package lab02.hunter.group4.a1;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

public class UserTest {
    private static User user;
    private static String expOutputJsonPath;
    @BeforeEach
    public void setUp() {
        expOutputJsonPath = "src/test/resources/expectedOutput.json";
        user = new User("user1", "123", true);
    }

    @Test
    public void testGetUsername() {
        Assertions.assertEquals("user1", user.getUsername());
    }

    @Test
    public void testGetPassword() {
        Assertions.assertEquals("123", user.getPassword());
    }

    @Test
    public void testGetIsAdmin() {
        Assertions.assertTrue(user.getIs_admin());
    }

    @Test
    public void testRegisterAdmin() {
        JsonManagement jm = new JsonManagement();
        JSONArray adminInfo = new JSONArray();
        JSONObject newAdmin = new JSONObject();
        newAdmin.put("username", "admin1");
        newAdmin.put("password", "Admin1");
        adminInfo.add(newAdmin);
        try {
            FileWriter file = new FileWriter(expOutputJsonPath);
            file.write(adminInfo.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User admin = User.register_admin("newAdmin", "Newadmin", true, expOutputJsonPath );
        HashMap<String, String> adminDB = jm.getAdminInfo(expOutputJsonPath);

        Assertions.assertEquals(adminDB.size(), 2);
        Assertions.assertTrue(admin.getIs_admin());
        Assertions.assertNotNull(adminDB.get("newAdmin"));
        Assertions.assertEquals(adminDB.get("newAdmin"), "Newadmin");
    }

    @Test
    public void testRegisterEmptyAdmin() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        User.register_admin("", "Newadmin", true, expOutputJsonPath);
        System.setOut(System.out);
        String expectedOutput = "Username cannot be empty!\n";
        Assertions.assertEquals(expectedOutput, outputStream.toString());

    }

    @Test
    public void testRegisterEmptyAdminPwd() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        User.register_admin("newAdmin2", "", true, expOutputJsonPath);
        System.setOut(System.out);
        String expectedOutput = "Password cannot be empty!\n";
        Assertions.assertEquals(expectedOutput, outputStream.toString());

    }

    @Test
    public void testRegisterNonAdmin() {
        User nonAdmin = User.register_admin("notAdmin", "notAdmin", false,expOutputJsonPath);
        Assertions.assertNull(nonAdmin);
    }

     @Test
     public void testLoginValidUser() {
         JSONArray adminInfo = new JSONArray();
         JSONObject newAdmin = new JSONObject();
         newAdmin.put("username", "admin1");
         newAdmin.put("password", "Admin1");
         adminInfo.add(newAdmin);
         try {
             FileWriter file = new FileWriter(expOutputJsonPath);
             file.write(adminInfo.toJSONString());
             file.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
         User loggedInUser = User.login("admin1", "Admin1", expOutputJsonPath);
         Assertions.assertNotNull(loggedInUser);
         Assertions.assertTrue(loggedInUser.getIs_admin());
     }

    @Test
    public void testLoginInvalidUser() {
        JSONArray adminInfo = new JSONArray();
        JSONObject newAdmin = new JSONObject();
        newAdmin.put("username", "admin1");
        newAdmin.put("password", "Admin1");
        adminInfo.add(newAdmin);
        try {
            FileWriter file = new FileWriter(expOutputJsonPath);
            file.write(adminInfo.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User loggedInUser = User.login("invalidUser", "invalidPassword", expOutputJsonPath);
        Assertions.assertNull(loggedInUser);
    }

}
