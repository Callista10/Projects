// import java.io.IOException;
// import java.sql.*;
// import java.nio.file.Files;
// import java.util.HashMap;
// import java.util.Map;
// import java.io.File;

// public class DigitalScrollManager {
//     private Map<String, DigitalScroll> scrolls;

//     public DigitalScrollManager() {
//         scrolls = new HashMap<>();
//         loadScrollsFromDatabase();
//     }

//     // Load existing scrolls from the database into memory.
//     private void loadScrollsFromDatabase() {
//         try (Connection conn = DatabaseConnector.getConnection();
//              PreparedStatement ps = conn.prepareStatement("SELECT * FROM scrolls")) {
//             ResultSet rs = ps.executeQuery();
//             while (rs.next()) {
//                 String id = rs.getString("id");
//                 String name = rs.getString("name");
//                 String uploaderId = rs.getString("uploaderId");
//                 File binaryFile = new File(rs.getString("binaryFile")); // Assuming binaryFile in database is stored as a filepath
//                 scrolls.put(id, new DigitalScroll(id, name, uploaderId, binaryFile));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//     }

//     // Add a new scroll
//     public void addScroll(String id, String name, String uploaderId, File binaryFile) {
//         DigitalScroll newScroll = new DigitalScroll(id, name, uploaderId, binaryFile);
//         scrolls.put(id, newScroll);

//         // Add to database
//         try (Connection conn = DatabaseConnector.getConnection();
//              PreparedStatement ps = conn.prepareStatement("INSERT INTO scrolls (id, name, uploaderId, binaryFile) VALUES (?, ?, ?, ?)")) {
//             ps.setString(1, id);
//             ps.setString(2, name);
//             ps.setString(3, uploaderId);
//             ps.setBytes(4, Files.readAllBytes(binaryFile.toPath()));
//             ps.executeUpdate();
//         } catch (SQLException | IOException e) {
//             e.printStackTrace();
//         }
//     }

//     // Edit an existing scroll
//     public void editScroll(String id, String uploaderId, String newName, File newBinaryFile) {
//         // Edit in-memory map
//         DigitalScroll scroll = scrolls.get(id);
//         if (scroll != null && scroll.getUploaderId().equals(uploaderId)) {
//             scroll.setName(newName);
//             scroll.setBinaryFile(newBinaryFile);
//         }

//         // Edit in database
//         try (Connection conn = DatabaseConnector.getConnection();
//              PreparedStatement ps = conn.prepareStatement("UPDATE scrolls SET name = ?, binaryFile = ? WHERE id = ? AND uploaderId = ?")) {
//             ps.setString(1, newName);
//             ps.setBytes(2, Files.readAllBytes(newBinaryFile.toPath()));
//             ps.setString(3, id);
//             ps.setString(4, uploaderId);
//             ps.executeUpdate();
//         } catch (SQLException | IOException e) {
//             e.printStackTrace();
//         }
//     }

//     // Remove a scroll
//     public void removeScroll(String id, String uploaderId) {
//         // Remove from in-memory map
//         scrolls.remove(id);

//         // Remove from database
//         try (Connection conn = DatabaseConnector.getConnection();
//              PreparedStatement ps = conn.prepareStatement("DELETE FROM scrolls WHERE id = ? AND uploaderId = ?")) {
//             ps.setString(1, id);
//             ps.setString(2, uploaderId);
//             ps.executeUpdate();
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//     }
// }

// Above is using SQL, below is using JSON. For Sprint 2 we will be using JSON.

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DigitalScrollManager {
    private ArrayList<Scroll> allScrolls;
    private String scrollDBPath;

    public DigitalScrollManager(String scrollDBPath) {
        this.allScrolls = JsonManagement.getScrollsJson(scrollDBPath);
        this.scrollDBPath = scrollDBPath;
    }

    // getter for allScrolls
    public ArrayList<Scroll> getAllScrolls() {
        return allScrolls;
    }

    // add new digital scroll to the database
    public void addScroll(Scroll targetScroll) {
        // error checking for targetScroll
        if (targetScroll == null) {
            System.out.println("Error: The scroll to be added is null");
            return;
        }

        // if the scroll already exists in the database, do not add it again
        for (Scroll scroll : allScrolls) {
            if (scroll.getScrollID().equals(targetScroll.getScrollID())) {
                System.out.println("Error: The scroll to be added already exists in the database");
                return;
            }
        }
        JsonManagement.writeNewScrollJson(scrollDBPath, targetScroll);
        allScrolls.add(targetScroll);
    }

    // edit an existing digital scroll in the database
    public void updateScrollContent(Scroll targetScroll, String newScrollContent) {
        // error checking for targetScroll
        if (targetScroll == null) {
            System.out.println("Error: The scroll to be updated is null");
            return;
        }
        // error checking for whether the provided scroll is in the database
        boolean scrollExists = false;
        for (Scroll scroll : allScrolls) {
            if (scroll.getScrollID().equals(targetScroll.getScrollID())) {
                scrollExists = true;
                break;
            }
        }
        if (!scrollExists) {
            System.out.println("Error: The scroll to be updated does not exist in the database");
            return;
        }

        // error checking of new Scroll content
        if (newScrollContent == null) {
            System.out.println("Error: The new scroll content is null");
            return;
        }

        // compare the new scroll content with the old one
        if (targetScroll.getScrollContent().equals(newScrollContent)) {
            System.out.println("Error: The new scroll content is the same as the old one");
            return;
        }

        // error checking of the new scroll content if it only contains 0s and 1s
        if (!newScrollContent.matches("[01]+")) {
            System.out.println("Error: The new scroll content contains invalid characters");
            return;
        }

        // update the scroll content
        targetScroll.setScrollContent(newScrollContent);
        JsonManagement.updateScrollContentJson(scrollDBPath, targetScroll, newScrollContent);
    }

    // remove an existing digital scroll from the database
    public void removeScroll(Scroll targetScroll) {
        // error checking for targetScroll
        if (targetScroll == null) {
            System.out.println("Error: The scroll to be removed is null");
            return;
        }
        // error checking for whether the provided scroll is in the database
        boolean scrollExists = false;
        for (Scroll scroll : allScrolls) {
            if (scroll.getScrollID().equals(targetScroll.getScrollID())) {
                scrollExists = true;
                break;
            }
        }
        if (!scrollExists) {
            System.out.println("Error: The scroll to be removed does not exist in the database");
            return;
        }
        for (Scroll scroll : allScrolls) {
            if (scroll.getScrollID().equals(targetScroll.getScrollID())) {
                allScrolls.remove(scroll);
                break;
            }
        }
        // convert allScrolls to JSONArray
        JSONArray allScrollsJson = new JSONArray();
        for (Scroll s: allScrolls) {
            JSONObject scrollObj = new JSONObject();
            scrollObj.put("scrollID", s.getScrollID());
            scrollObj.put("scrollName", s.getScrollName());
            scrollObj.put("scrollUploaderID", s.getScrollUploaderID());
            scrollObj.put("scrollUploadDate", s.getScrollUploadDate());
            scrollObj.put("noOfDownloads", s.getNoOfDownloads());
            scrollObj.put("scrollContent", s.getScrollContent());
            allScrollsJson.add(scrollObj);
        }
        JsonManagement.writeToJson(scrollDBPath, allScrollsJson);

    }


    // public static void main(String[] args) {
    //     // test addScroll()
    //     String json_path = "src/main/resources/Scrolls.json";
    //     Scroll newScr = new Scroll("564378055", "test", "test", null, 0, "0011010010101001");
    //     DigitalScrollManager dsm = new DigitalScrollManager(json_path);
    //     dsm.addScroll(newScr);
    //     dsm.updateScrollContent(newScr, "110110");
    //     dsm.removeScroll(newScr);
    // }
}
