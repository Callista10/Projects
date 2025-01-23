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

public class ScrollSeekerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ScrollSeeker scrollSeeker;
    private ArrayList<Scroll> originalDB;

    @BeforeEach
    public void setUp() {
        // Initialize ScrollSeeker with the test database path
        scrollSeeker = new ScrollSeeker("src/test/resources/TestScrolls.json");
        // store all scrolls in the test database in allScrolls
        originalDB = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
    }

    // Test viewAllScrolls() when there are no scrolls in the database
    @Test
    public void testViewAllScrollsWhenScrollListIsNull() {
        setUp();
        
        JSONArray nullScrollList = new JSONArray();
        JsonManagement.writeToJson("src/test/resources/TestScrolls.json", nullScrollList);
        System.setOut(new PrintStream(outContent));
        ScrollSeeker scrollSeeker = new ScrollSeeker("src/test/resources/TestScrolls.json");
        scrollSeeker.viewAllScrolls();
        Assertions.assertTrue(outContent.toString().contains("No scrolls are available."));
        // restore the scroll database
        restoreStreams();
    }

    // Test viewAllScrolls() when there are scrolls in the database
    @Test
    public void testViewAllScrollValid() throws IOException {
        setUp();
        
        // store the expected output in a file
        String expectedFilePath = "src/test/resources/expectedOutput.txt";
        // write expected output in a text file
        FileOutputStream expectedOutput = new FileOutputStream(expectedFilePath);
        expectedOutput.write(outContent.toString().getBytes());
        expectedOutput.write(("""
        ================AVAILABLE SCROLLS================
        --------------------------------------------------
        Scroll ID: 418808658
        Scroll Name: Scroll1
        Scroll Uploader ID: admin
        Scroll Upload Date: 2023/10/23
        No. of Downloads: 0
        --------------------------------------------------
        Scroll ID: 674381076
        Scroll Name: Scroll1
        Scroll Uploader ID: admin
        Scroll Upload Date: 2023/10/23
        No. of Downloads: 0
        ==================================================
        """).getBytes());
        expectedOutput.close();

        // store the actual output in a file
        String actualFilePath = "src/test/resources/actualOutput.txt";
        try {
            FileOutputStream actualOutput = new FileOutputStream(actualFilePath);
            System.setOut(new PrintStream(outContent));
            scrollSeeker.viewAllScrolls();
            actualOutput.write(outContent.toString().trim().getBytes());
            actualOutput.close();
        } catch (Exception e) {
            System.out.println("No scrolls are available.");
        }
        
        // compare the two files
        Assertions.assertEquals(new String(outContent.toByteArray()).trim(), new String(outContent.toByteArray()).trim());

        // restore the scroll database
        restoreStreams();
    }
    
    // Test findscrollbyid() when the scroll id is valid
    @Test
    public void testFindScrollByIdValid() {
        setUp();
        // current scroll database
        ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
        // create a new scroll and add it to the database
        Scroll scrollExpected = new Scroll("ValidScroll", "tester", "2023/10/23", "12345678");
        // expected scroll id
        String expScoreID = scrollExpected.getScrollID();
        // add the scroll to the database
        allScrolls.add(scrollExpected);
        Scroll scrollActual = scrollSeeker.findScrollById(allScrolls, expScoreID);
        Assertions.assertEquals(scrollExpected, scrollActual);
        restoreStreams();
    }

    // Test findscrollbyid() when the scroll id does not exist
    @Test
    public void testFindScrollByIdInvalid() {
        setUp();
        // current scroll database
        ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/test/resources/TestScrolls.json");
        // randomly create a 9-digit scroll id that does not exist in the database
        boolean exist = true;
        String randomScrollID;
        while (exist) {
            randomScrollID = String.valueOf((int) (Math.random() * 1000000000));
            for (Scroll scroll : allScrolls) {
                if (scroll.getScrollID().equals(randomScrollID)) {
                    continue;
                } else {
                    Assertions.assertNull(scrollSeeker.findScrollById(allScrolls, randomScrollID));
                    exist = false;
                    break;
                }
            }
        }
        restoreStreams();
    }

    // Test downloadScroll() when the scroll id is invalid
    
    // Test downloadScroll() when the scroll id is valid

    // Test search by uploader id when the uploader id is null
    @Test
    public void testSearchByUploaderIDNull() {
        setUp();
        
        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByUploaderID(null);
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid uploader ID."));
        // restore the scroll database
        restoreStreams();
    }

    @Test
    public void testSearchByUploaderIDEmptyDB() {
        setUp();
        
        JSONArray nullScrollList = new JSONArray();
        JsonManagement.writeToJson("src/test/resources/TestScrolls.json", nullScrollList);
        System.setOut(new PrintStream(outContent));
        ScrollSeeker scrollSeeker = new ScrollSeeker("src/test/resources/TestScrolls.json");
        scrollSeeker.searchByUploaderID("admin");
        Assertions.assertTrue(outContent.toString().contains("No scrolls are available."));
        Assertions.assertNull(scrollSeeker.searchByUploaderID("admin"));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by uploader id when the uploader id is valid but does not upload any scrolls
    @Test
    public void testSearchByUploaderIDValidButNoScrolls() {
        setUp();
        ArrayList<User> allUsers = JsonManagement.getUsersJson("src/test/resources/TestUsers.json");
        ArrayList<String> allUsersID = new ArrayList<>();
        for (User user : allUsers) {
            allUsersID.add(user.getCustomIDKey());
        }
        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByUploaderID("non-existUsersID");
        Assertions.assertTrue(outContent.toString().contains("No scrolls were found with the uploader ID: non-existUsersID"));
        Assertions.assertNull(scrollSeeker.searchByUploaderID("non-existUsersID"));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by uploader id when the uploader id is valid and uploads scrolls
    @Test
    public void testSearchByUploaderIDValidAndHasScrolls() throws IOException {
        setUp();
        
        // store the expected output in a file
        String expectedFilePath = "src/test/resources/expectedOutput.txt";
        try (FileOutputStream expectedOutput = new FileOutputStream(expectedFilePath)) {
            String expectedContent = String.format(
            """
            ================SEARCH RESULTS FOR UPLOADER ID: admin ================
            --------------------------------------------------
            Uploader ID: admin
            Scroll ID: %s
            Scroll Name: Scroll1
            Scroll Upload Date: 2023/10/24
            No. of Downloads: 0
            --------------------------------------------------
            Uploader ID: admin
            Scroll ID: %s
            Scroll Name: Scroll1
            Scroll Upload Date: 2023/10/24
            No. of Downloads: 0
            ==================================================
            """,
            originalDB.get(0).getScrollID(),
            originalDB.get(1).getScrollID());

            expectedOutput.write(expectedContent.getBytes());
        }
        // store the actual output in a file
        String actualFilePath = "src/test/resources/actualOutput.txt";
        try {
            FileOutputStream actualOutput = new FileOutputStream(actualFilePath);
            System.setOut(new PrintStream(outContent));
            scrollSeeker.searchByUploaderID("admin");
            actualOutput.write(outContent.toString().trim().getBytes());
            actualOutput.close();
        } catch (Exception e) {
            System.out.println("No scrolls are available.");
        }

        // compare the two files
        String expectedContent = new String(Files.readAllBytes(Paths.get(expectedFilePath)));
        String actualContent = new String(Files.readAllBytes(Paths.get(actualFilePath)));
        Assertions.assertEquals(expectedContent.trim(), actualContent.trim());

        // restore the scroll database
        restoreStreams();
    }
    
    // Test search by scroll id when the scroll id is null
    @Test
    public void testSearchBySearchIDNull() {
        setUp();
        
        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByScrollID(null);
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid scroll ID."));
        // restore the scroll database
        restoreStreams();
    }
    
    // Test search by scroll id when the scroll id is not 9 digit long
    @Test
    public void testSearchBySearchIDInvalid() {
        setUp();
        
        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByScrollID("1234567");
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid scroll ID."));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll id when the scroll id is valid but does not exist in the database
    @Test
    public void testScrollIDNotExist() {
        setUp();

        System.setOut(new PrintStream(outContent));
        String nonExistScrollID;
        boolean exist = true;
        while (exist) {
            nonExistScrollID = String.valueOf((int) (Math.random() * 1000000000));
            for (Scroll scroll : originalDB) {
                if (scroll.getScrollID().equals(nonExistScrollID)) {
                    continue;
                } else {
                    scrollSeeker.searchByScrollID(nonExistScrollID);
                    Assertions.assertTrue(outContent.toString().contains("No scrolls were found with the scroll ID: " + nonExistScrollID));
                    Assertions.assertNull(scrollSeeker.searchByScrollID(nonExistScrollID));
                    exist = false;
                    break;
                }
            }
        }
        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll id when the scroll id is valid and exists in the database
    @Test
    public void testscrollIDValid() {
        setUp();

        System.setOut(new PrintStream(outContent));
        String existScrollID = originalDB.get(0).getScrollID();
        scrollSeeker.searchByScrollID(existScrollID);
        Assertions.assertTrue(outContent.toString().contains("SCROLL ID: " + existScrollID));

        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll name when the scroll name is null
    @Test
    public void testScrollNameNull() {
        setUp();

        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByScrollName(null);
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid scroll name."));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll name when the scroll name is valid
    @Test
    public void testScrollNameValid() {
        setUp();

        System.setOut(new PrintStream(outContent));
        String existScrollName = originalDB.get(0).getScrollName();
        scrollSeeker.searchByScrollName(existScrollName);
        Assertions.assertTrue(outContent.toString().contains("SCROLL NAME: '" + existScrollName));

        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll upload date when it is null
    @Test
    public void testScrollDateNull() {
        setUp();

        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByScrollUploadDate(null);
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid scroll upload date."));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll upload date when it is invalid
    @Test
    public void testScrollDateInvalid() {
        setUp();

        System.setOut(new PrintStream(outContent));
        scrollSeeker.searchByScrollUploadDate("2023/10/32");
        Assertions.assertTrue(outContent.toString().contains("Please enter a valid scroll upload date in the format: yyyy/MM/dd"));
        // restore the scroll database
        restoreStreams();
    }

    // Test search by scroll upload date when it is valid
    @Test
    public void testScrollDateValid() {
        setUp();

        System.setOut(new PrintStream(outContent));
        String existScrollDate = originalDB.get(0).getScrollUploadDate();
        scrollSeeker.searchByScrollUploadDate(existScrollDate);
        Assertions.assertTrue(outContent.toString().contains("SCROLL UPLOAD DATE: " + existScrollDate));

        // restore the scroll database
        restoreStreams();
    }

    // Test preview scroll
    @Test
    public void testPreviewScroll() {
        setUp();

        System.setOut(new PrintStream(outContent));
        Scroll existScroll = originalDB.get(0);
        scrollSeeker.previewScroll(existScroll);
        Assertions.assertTrue(outContent.toString().contains("Preview for Scroll ID: " + existScroll.getScrollID()));

        // restore the scroll database
        restoreStreams();
    }

    // Test scrollOfTheDay when no scrolls are available
    @Test
    public void testScrollOfTheDayEmptyDB() {
        setUp();

        JSONArray nullScrollList = new JSONArray();
        JsonManagement.writeToJson("src/test/resources/TestScrolls.json", nullScrollList);
        System.setOut(new PrintStream(outContent));
        ScrollSeeker scrollSeeker = new ScrollSeeker("src/test/resources/TestScrolls.json");
        scrollSeeker.scrollOfTheDay();
        Assertions.assertTrue(outContent.toString().contains("No scrolls are available."));
        // restore the scroll database
        restoreStreams();
    }

    // Test scrollOfTheDay when there are scrolls in the database
    @Test
    public void testScrollOfTheDayValid() {
        setUp();

        System.setOut(new PrintStream(outContent));
        scrollSeeker.scrollOfTheDay();
        Assertions.assertTrue(outContent.toString().contains("Scroll of the Day"));

        // restore the scroll database
        restoreStreams();
    }
    // reset the streams and restore the database
    @AfterEach
    public void restoreStreams() {
        // reset the streams after the tests
        System.setOut(originalOut);

        // restore the scroll database
        JSONArray allScrollsJson = new JSONArray();
        for (Scroll scroll : originalDB) {
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
}

