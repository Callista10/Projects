import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Scanner;

public class AppTest {
    String[] args = null;
    String inputSeparator = System.getProperty("line.separator");
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ArrayList<Scroll> originalDBScroll;
    private ArrayList<User> originalDBUser;
    private App app;

    @BeforeEach
    public void setUp() {
        // store all scrolls in the test database in allScrolls
        originalDBScroll = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
        originalDBUser = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
    }

    @AfterEach
    public void restoreStreams() {
        // reset the streams after the tests
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent.reset();

        // restore the scroll database
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

        // restore the user database
        JSONArray allUsersJson = new JSONArray();
        for (User user : originalDBUser) {
            JSONObject userJson = new JSONObject();
            userJson.put("customIDKey", user.getCustomIDKey());
            userJson.put("hashedPassword", user.getPassword());
            userJson.put("fullName", user.getFullName());
            userJson.put("email", user.getEmailAddress());
            userJson.put("phone", user.getPhone());
            if (user instanceof GeneralUser) {
                userJson.put("type", "general");
            } else if (user instanceof AdminUser) {
                if (user instanceof RootAdminUser) {
                    userJson.put("type", "rootadmin");
                } else {
                    userJson.put("type", "admin");
                }
            }
            allUsersJson.add(userJson);
        }
    }


    // Test for getUsertype()
    @Test
    public void testGetUsertype() {
        // test for general user
        setUp();
        
        String type;
        User general;
        User admin;
        User rootAdmin;
        for (User userObj: originalDBUser) {
            if (userObj instanceof GeneralUser) {
                general = userObj;
                type = app.getUserType(general);
                Assertions.assertEquals(type, "General");
            }
            else if (userObj instanceof AdminUser) {
                if (userObj instanceof RootAdminUser) {
                    rootAdmin = userObj;
                    type = app.getUserType(rootAdmin);
                    Assertions.assertEquals(type, "RootAdmin");
                } else {
                    admin = userObj;
                    type = app.getUserType(admin);
                    Assertions.assertEquals(type, "Admin");
                }
            }
        }
        // restore the scroll database
        restoreStreams();
    }

    // Test handleLoggedInGeneralUser()
    // @Test
    // public void testHandleLoggedInGeneralUserNull() throws IOException {
    //     // test for general user
    //     setUp();
    //     ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
    //     // set simulated user input
    //     // ===== case 1: user wants to view and download a scroll =====
    //     // ----- null scrollID ----------------------------------------
    //     String simulatedUserInput = "1\n" + "\n" + "7\n";
    //     System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
    //     // write the output to a file
    //     String actualFilePath = "src/test/resources/actualOutput.txt";
    //     FileOutputStream actualOutput = new FileOutputStream(actualFilePath);
    //     System.setOut(new PrintStream(outContent));
    //     app.handleLoggedInGeneralUser("src/test/resources/TestScrolls.json");
    //     actualOutput.write(outContent.toString().trim().getBytes());
    //     actualOutput.close();
        
    //     // actual output should contain "Scroll with ID  not found."
    //     Assertions.assertTrue(outContent.toString().contains("Scroll with ID  not found."));
    
    //     // restore the scroll database
    //     restoreStreams();
    // }

    // Test handleLoggedInAdminUser(), case 1: user wants to view and download a scroll | not null scrollID
    @Test
    public void testHandleLoggedInGeneralUserNotNull() throws IOException {
        setUp();
        ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
        String simulatedUserInput2 = "1\n" + allScrolls.get(0).getScrollID() + "\n7\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput2.getBytes()));
        // write the output to a file
        String actualFilePath = "src/test/resources/actualOutput.txt";
        FileOutputStream actualOutput = new FileOutputStream(actualFilePath);
        System.setOut(new PrintStream(outContent));
        app.handleLoggedInGeneralUser("src/test/resources/TestScrolls.json");
        actualOutput.write(outContent.toString().trim().getBytes());
        actualOutput.close();
        Assertions.assertTrue(outContent.toString().contains("has been downloaded and saved as"));
        restoreStreams();
    }
}
