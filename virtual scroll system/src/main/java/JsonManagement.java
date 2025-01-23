
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonManagement {

////////////////////////// 1. JSON Functions //////////////////////////

    // helper function to read json file and return a json object
    public static Object readJson(String jsonPath) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            File file = new File(jsonPath);
            // read json file
            FileReader reader = new FileReader(file);
            // convert json file to json object
            obj = parser.parse(reader);
            reader.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return obj;
    }

    // helper function to write or update json file
    public static void writeToJson(String DBPath, JSONArray fullContent) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(new File(DBPath), fullContent);
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

////////////////////////// 2. User Management //////////////////////////

    // Function that obtain all the users from the json file
    public static ArrayList<User> getUsersJson(String userDBPath) {
        Object obj = readJson(userDBPath);
        ArrayList<User> allUsers = new ArrayList<User>();
        // User file always contains at least one user, which is the root admin
        // when the user file contains only one user
        // if (obj instanceof JSONObject) {
        //     JSONObject userJson = (JSONObject) obj;
        //     String userType = (String) userJson.get("type");
        //     String customID = (String) userJson.get("customID");
        //     String hashedPwd = (String) userJson.get("hashedPassword");
        //     String fullName = (String) userJson.get("fullName");
        //     String email = (String) userJson.get("email");
        //     String phone = (String) userJson.get("phone");
        //     User user = createUser(userType, customID, hashedPwd, fullName, email, phone);
        //     allUsers.add(user);
        // }
        // when the current user database contains more than one user
        if (obj instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) obj;
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject userJson = (JSONObject) usersJson.get(i);
                String userType = (String) userJson.get("type");
                String customID = (String) userJson.get("customID");
                String hashedPwd = (String) userJson.get("hashedPassword");
                String fullName = (String) userJson.get("fullName");
                String email = (String) userJson.get("email");
                String phone = (String) userJson.get("phone");
                User user = createUser(userType, customID, hashedPwd, fullName, email, phone);
                allUsers.add(user);
            }
        }
        else {
            System.out.println("Error: The user file is not in the correct format");
            return null;
        }
        return allUsers;
    }

    // helper function to create new user according to the user type
    public static User createUser(String userType, String customID, String hashedPwd, String fullName, String email, String phone) {
        if (userType.toLowerCase().equals("general")) {
            GeneralUser user = new GeneralUser(customID, hashedPwd, fullName, email, phone);
            return user;
        }
        else if (userType.toLowerCase().equals("admin")) {
            AdminUser user = new AdminUser(customID, hashedPwd, fullName, email, phone);
            return user;
        }
        else if (userType.toLowerCase().equals("rootadmin")) {
            RootAdminUser user = new RootAdminUser(customID, hashedPwd, fullName, email, phone);
            return user;
        }
        else {
            System.out.println("Error: The user type is not valid");
            return null;
        }
    }

    // Function that write a user to the json file
    public static void writeNewUserJson(String userDBPath, User user) {
        ArrayList<User> allUsers = getUsersJson(userDBPath);
        // assume error checking is done in the User class
        allUsers.add(user);
        JSONArray allUsersJson = new JSONArray();
        for (User userObj : allUsers) {
            JSONObject userJson = new JSONObject();
            if (userObj instanceof GeneralUser) {
                userJson.put("type", "general");
            }
            else if (userObj instanceof AdminUser) {
                if (userObj instanceof RootAdminUser) {
                    userJson.put("type", "rootadmin");
                }
                else {
                    userJson.put("type", "admin");
                }
            }
            else {
                System.out.println("Error: The user type is not valid");
                return;
            }
            
            userJson.put("customID", userObj.getCustomIDKey());
            userJson.put("hashedPassword", userObj.getPassword());
            userJson.put("fullName", userObj.getFullName());
            userJson.put("email", userObj.getEmailAddress());
            userJson.put("phone", userObj.getPhone());
            allUsersJson.add(userJson);
        }
        writeToJson(userDBPath, allUsersJson);
    }

    // Function that update user profile in the json file
    public static void updateUserProfileJson(String customID, String filter, String newInfo, String userDBPath) {
        // assume error checking is done in the User class
        ArrayList<User> allUsers = getUsersJson(userDBPath);
        // update phone, hashed password, type (only admin can do this), full name, email
        for (User userObj: allUsers) {
            if (userObj.getCustomIDKey().equals(customID)) {
                // update phone
                if (filter.equals("phone")) {
                    userObj.setPhone(newInfo);
                } 
                // update password
                else if (filter.equals("hashedPassword")) {
                    userObj.setPassword(newInfo);
                }
                // TODO: update type - only admin can perform this action
                // update full name
                else if (filter.equals("fullName")) {
                    userObj.setFullName(newInfo);
                }
                // update email
                else if (filter.equals("email")) {
                    userObj.setEmailAddress(newInfo);
                }
                else {
                    System.out.println("Error: The filter is not valid");
                    return;
                }
            } 
            else {
                continue;
            }
        }
        JSONArray allUsersJson = new JSONArray();
        for (User userObj : allUsers) {
            JSONObject userJson = new JSONObject();
            if (userObj instanceof GeneralUser) {
                userJson.put("type", "general");
            }
            else if (userObj instanceof AdminUser) {
                if (userObj instanceof RootAdminUser) {
                    userJson.put("type", "rootadmin");
                }
                else {
                    userJson.put("type", "admin");
                }
            }
            else {
                System.out.println("Error: The user type is not valid");
                return;
            }
            
            userJson.put("customID", userObj.getCustomIDKey());
            userJson.put("hashedPassword", userObj.getPassword());
            userJson.put("fullName", userObj.getFullName());
            userJson.put("email", userObj.getEmailAddress());
            userJson.put("phone", userObj.getPhone());
            allUsersJson.add(userJson);
        }
        writeToJson(userDBPath, allUsersJson);
    }

    ////////////////////////// 3. Scroll Management //////////////////////////

    // Function that obtain all the scrolls from the json file
    public static ArrayList<Scroll> getScrollsJson(String scrollDBPath) {
        Object obj = readJson(scrollDBPath);
        ArrayList<Scroll> allScrolls = new ArrayList<Scroll>();
        // when the scroll file is empty
        if (obj == null) {
            System.out.println("Error: The scroll file is empty");
            return null;
        }
        // // when the scroll file contains only one scroll
        // else if (obj instanceof JSONObject) {
        //     JSONObject scrollJson = (JSONObject) obj;
        //     String scrollIdString = (String) scrollJson.get("scrollID");
        //     String scrollName = (String) scrollJson.get("scrollName");
        //     String scrollUploaderID = (String) scrollJson.get("scrollUploaderID");
        //     String scrollUploadDate = (String) scrollJson.get("scrollUploadDate");
        //     int noOfDownloads = Integer.parseInt((String) scrollJson.get("noOfDownloads"));
        //     String scrollContent = (String) scrollJson.get("scrollContent");
        //     String scrollPwd = (String) scrollJson.get("scrollPwd");
        //     Scroll scroll = new Scroll(scrollIdString, scrollName, scrollUploaderID, scrollUploadDate, noOfDownloads, scrollContent, scrollPwd);
        //     allScrolls.add(scroll);
        //     return allScrolls;
        // }
        // when the current scroll database contains more than one scroll
        if (obj instanceof JSONArray) {
            JSONArray scrollsJson = (JSONArray) obj;
            for (int i = 0; i < scrollsJson.size(); i++) {
                JSONObject scrollJson = (JSONObject) scrollsJson.get(i);
                String scrollIdString = (String) scrollJson.get("scrollID");
                String scrollName = (String) scrollJson.get("scrollName");
                String scrollUploaderID = (String) scrollJson.get("scrollUploaderID");
                String scrollUploadDate = (String) scrollJson.get("scrollUploadDate");
                Long noOfDownloadsLong = (Long) scrollJson.get("noOfDownloads");
                // convert Long to int
                int noOfDownloads = noOfDownloadsLong.intValue();
                String scrollContent = (String) scrollJson.get("scrollContent");
                String scrollPwd = (String) scrollJson.get("scrollPwd");
                Scroll scroll = new Scroll(scrollIdString, scrollName, scrollUploaderID, scrollUploadDate, noOfDownloads, scrollContent, scrollPwd);
                allScrolls.add(scroll);
            }
            return allScrolls;
        }
        else {
            System.out.println("Error: The scroll file is not in the correct format");
            return null;
        }

    }

    // Function that write a scroll to the json file
    public static void writeNewScrollJson(String scrollDBPath, Scroll scroll) {
        ArrayList<Scroll> allScrolls = getScrollsJson(scrollDBPath);
        // assume error checking is done in the Scroll class
        allScrolls.add(scroll);
        // even when allScrolls has a size of 1, it will still be written as a JSONArray
        JSONArray allScrollsJson = new JSONArray();
        for (Scroll scrollObj : allScrolls) {
            JSONObject scrollJson = new JSONObject();
            scrollJson.put("scrollID", scrollObj.getScrollID());
            scrollJson.put("scrollName", scrollObj.getScrollName());
            scrollJson.put("scrollUploaderID", scrollObj.getScrollUploaderID());
            scrollJson.put("scrollUploadDate", scrollObj.getScrollUploadDate());
            scrollJson.put("noOfDownloads", scrollObj.getNoOfDownloads());
            scrollJson.put("scrollContent", scrollObj.getScrollContent());
            scrollJson.put("scrollPwd", scrollObj.getScrollPassword());
            allScrollsJson.add(scrollJson);
        }
        writeToJson(scrollDBPath, allScrollsJson);
    }

    // Function that update scroll content in the json file
    public static void updateScrollContentJson(String scrollDBPath, Scroll scroll, String newScrollContent) {
        ArrayList<Scroll> allScrolls = getScrollsJson(scrollDBPath);
        // assume error checking is done in the Scroll class
        for (Scroll scrollObj : allScrolls) {
            if (scrollObj.getScrollID().equals(scroll.getScrollID())) {
                scrollObj.setScrollContent(newScrollContent);
                break;
            }
        }
        // even when allScrolls has a size of 1, it will still be written as a JSONArray
        JSONArray allScrollsJson = new JSONArray();
        for (Scroll scrollObj : allScrolls) {
            JSONObject scrollJson = new JSONObject();
            scrollJson.put("scrollID", scrollObj.getScrollID());
            scrollJson.put("scrollName", scrollObj.getScrollName());
            scrollJson.put("scrollUploaderID", scrollObj.getScrollUploaderID());
            scrollJson.put("scrollUploadDate", scrollObj.getScrollUploadDate());
            scrollJson.put("noOfDownloads", scrollObj.getNoOfDownloads());
            scrollJson.put("scrollContent", scrollObj.getScrollContent());
            scrollJson.put("scrollPwd", scrollObj.getScrollPassword());
            allScrollsJson.add(scrollJson);
        }
        writeToJson(scrollDBPath, allScrollsJson);
    }

    
    public static void main(String[] args) {
        // test updateuserprofilejson
        String userDBPath = "src/main/resources/User.json";
        updateUserProfileJson("Fiona", "email", "fionachen@qq.com", userDBPath);
    }
}