import java.util.Scanner;
import java.util.ArrayList;

class RootAdminUser extends AdminUser {

    private String customIDKey;
    private String password;
    private String fullName;
    private String emailAddress;
    private String phone;

    public RootAdminUser(String customIDKey, String password, String fullName, String emailAddress, String phone) {
        super(customIDKey, password, fullName, emailAddress, phone);
    }


    public boolean isAdmin() {
        return true; // By default, regular users are not admins
    }


    public static void addGeneralUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create a new user:");
        System.out.print("Custom ID: ");
        String customIDKey = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        String hashedPassword = UserManagement.hashPassword(password);

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String emailAddress = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        // Create and add the new user
        GeneralUser newUserma = new GeneralUser(customIDKey, hashedPassword, fullName, emailAddress, phone);
        UserManagement.addNewUserToDB(newUserma);

        System.out.println("New user created.");
    }

    public static void deleteGeneralUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CustomIDKey of the existing GeneralUser who you want to delete: ");
        String customIDKey = scanner.nextLine();
        ArrayList<User> allUsers = UserManagement.getAllUsers();

        for (User user : UserManagement.getAllUsers()) {
            if (user instanceof GeneralUser && user.getCustomIDKey().equals(customIDKey)) {
                allUsers.remove(user);
                System.out.println("You deleted the user with CustomIDKey: " + customIDKey);
                return;
            }
        }
        System.out.println("User with CustomIDKey: " + customIDKey + " not found.");
    }

    // Method to grant privileges to an existing GeneralUser by AdminUser
    static void grantPrivilegesByAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CustomIDKey of the existing GeneralUser to grant privileges: ");
        String customIDKey = scanner.nextLine();
        ArrayList<User> allUsers = UserManagement.getAllUsers();

        for (User user : allUsers) {
            if (user instanceof GeneralUser && user.getCustomIDKey().equals(customIDKey)) {
                AdminUser adminUser = new AdminUser(
                        user.getCustomIDKey(),
                        user.getPassword(),
                        user.getFullName(),
                        user.getEmailAddress(),
                        user.getPhone()
                );
                allUsers.remove(user);
                allUsers.add(adminUser);
                System.out.println("Privileges granted to the user with CustomIDKey: " + customIDKey);
                return;
            }
        }
        System.out.println("User with CustomIDKey: " + customIDKey + " not found.");
    }

    public static void deleteAdminUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the CustomIDKey of the existing AdminUser you want to delete: ");
        String customIDKey = scanner.nextLine();
        ArrayList<User> allUsers = UserManagement.getAllUsers();

        for (User user : allUsers) {
            if (user instanceof AdminUser && user.getCustomIDKey().equals(customIDKey)) {
                allUsers.remove(user);
                System.out.println("You deleted the admin user with CustomIDKey: " + customIDKey);
                return;
            }
        }
        System.out.println("User with CustomIDKey: " + customIDKey + " not found.");
    }


    public static void addAdminUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create a new admin user:");
        System.out.print("Custom ID: ");
        String customIDKey = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        String hashedPassword = UserManagement.hashPassword(password);

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String emailAddress = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        // Create and add the new user
        AdminUser newAdminUser = new AdminUser(customIDKey, hashedPassword, fullName, emailAddress, phone);
        UserManagement.addNewUserToDB(newAdminUser);

        System.out.println("New user created.");
    }
    // Method to list all users and their profiles

    public static void adminProfile() {
        //Admin Users
        //Make a single admin profile that has access to special admin privileges.
        ArrayList<User> allUsers = UserManagement.getAllUsers();
        for (User user : allUsers) {
            if (user instanceof AdminUser) {
                System.out.println("----------------------------------------");
                System.out.println("You has access to special admin privileges");
                System.out.println("Custom ID: " + user.getCustomIDKey());
                System.out.println("Full Name: " + user.getFullName());
                System.out.println("Email: " + user.getEmailAddress());
                System.out.println("Phone: " + user.getPhone());
                System.out.println("----------------------------------------");
            }
        }
    }

    public static void removeScroll() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the scrollId of the existing scroll you want to delete: ");
        String scrollId = scanner.nextLine();
        DigitalScrollManager scrollManager = new DigitalScrollManager("src/main/resources/Scrolls.json");
        Scroll scrollToRemove = null;

        for (Scroll scroll : scrollManager.getAllScrolls()) {
            if (scroll.getScrollID().equals(scrollId)) {
                scrollToRemove = scroll;
                break;
            }
        }

        if (scrollToRemove != null) {
            scrollManager.removeScroll(scrollToRemove);
        } else {
            System.out.println("Scroll with scrollId: " + scrollId + " not found.");
        }
    }
}
