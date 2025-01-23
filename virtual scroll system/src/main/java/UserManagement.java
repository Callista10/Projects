

import java.util.ArrayList;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class UserManagement {
    // extract all user from json
    private static User loggedInUser = null;
    static String userDBPath = "src/main/resources/User.json";
    private static ArrayList<User> allUsers;

    public UserManagement(String userDBPath) {
        UserManagement.userDBPath = userDBPath;
        allUsers = JsonManagement.getUsersJson(userDBPath);
    }

    // getter for allUsers
    public static ArrayList<User> getAllUsers() {
        return JsonManagement.getUsersJson(userDBPath);
    }

    public static void addNewUserToDB(User newUser) {
        allUsers = JsonManagement.getUsersJson(userDBPath);
        // error checking of newUser if it is null
        if (newUser == null) {
            System.out.println("Error: New user cannot be null");
            return;
        }

        // error checking if newUser already exists
        for (User user: allUsers) {
            if (user.getCustomIDKey().equals(newUser.getCustomIDKey())) {
                System.out.println("Error: Username already exists");
                return;
            }
        }
        JsonManagement.writeNewUserJson(userDBPath, newUser);
    }

    // public static void main(String[] args) {

    //     String generalHashedPassword = hashPassword("1234"); // Hash the password before storing it
    //     GeneralUser generalUser2 = new GeneralUser("GeneralMA", generalHashedPassword, "General Ma", "112@example.com", "987654321");
    //     users.add(generalUser2);

    //     String adminHashedPassword2 = hashPassword("admin1234"); // Hash the password before storing it
    //     RootAdminUser adminUser2 = new RootAdminUser("admin2", adminHashedPassword2, "Admin Chen", "admin@example.com", "987654321");
    //     users.add(adminUser2);

    //     // Check if an admin is logged in and allow admin functions
    //     if (loggedInUser != null && loggedInUser.isAdmin()) {
    //         System.out.println("Admin privileges enabled.");

    //         AdminUser.adminProfile();
    //         AdminUser.deleteGeneralUser();
    //         AdminUser.addGeneralUser();
    //         // Admin-specific actions here
    //         AdminUser.grantPrivilegesByAdmin();
    //         AdminUser.addAdminUser();

    //         createUser();

    //     } else {
    //         System.out.println("Regular user privileges.");
    //         // Regular user actions here
    //     }



    // }

    // Hashing the password using bcrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Validate user login
    public static User loginUser(String username, String password) {
        allUsers = JsonManagement.getUsersJson(userDBPath);
        for (User user : allUsers) {
            // TODO: should check the customerID not full name
            if (user.getCustomIDKey().equals(username) && BCrypt.checkpw(password, user.getPassword())) {
                loggedInUser = user; // Set the currently logged-in user
                System.out.println("Logged in as: " + loggedInUser.getCustomIDKey());
                return user; // Successfully logged in
            }
        }
        System.out.println("Sorry, the ID or password is incorrect T T") ;
        return null; // Login failed
    }

    public static void createUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create a new general user:");
        System.out.print("Custom ID: ");
        String customIDKey = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String emailAddress = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        // Create and add the new user
        GeneralUser newUserma = new GeneralUser(customIDKey, hashedPassword, fullName, emailAddress, phone);
        allUsers.add(newUserma);
        // Write the new user to the JSON file
        UserManagement.addNewUserToDB(newUserma);
        System.out.println("New user created.");
    }


}
