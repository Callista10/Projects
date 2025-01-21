package lab02.hunter.group4.a1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.HashMap;

public class User {
    private static String username;
    private static String password;
    private static Boolean is_admin;
    private static JsonManagement jm = new JsonManagement();

    public User(String username, String password, Boolean is_admin){
        this.username = username;
        this.password = password;
        this.is_admin = is_admin;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public Boolean getIs_admin(){
        return is_admin;
    }



    public static User register_admin(String username, String password, Boolean is_admin, String adminPath){
        if(!is_admin){
            System.out.println("Only admin can register other admins");
            return null;
        }
        else if (username == null || username.isEmpty()) {
            System.out.println("Username cannot be empty!");
            return null;
        }
        else if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty!");
            return null;
        }
        else if(username != null){
            HashMap<String, String> adminInfo = jm.getAdminInfo(adminPath);
            if(adminInfo.containsKey(username)){
                System.out.println("Username already exist!");
            }
        }

        JsonManagement jm = new JsonManagement();
        jm.addAdmin(username, password, adminPath);
        return new User(username, password, true);
    }

    public static User login(String username, String password, String adminPath){
        try{

            HashMap<String, String> adminInfo = jm.getAdminInfo(adminPath);
            if(adminInfo.containsKey(username)){
                if(adminInfo.get(username).equals(password)) {
                    System.out.println("Login successful");
                    return new User(username, password, true);
                }
                else{
                    System.out.println("Incorrect password");
                }
            }
            else{
                System.out.println("Username not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void logout(){
        System.out.println("Logging out...");
        System.exit(0);
    }

}
