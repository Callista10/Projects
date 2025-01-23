/*
 * This Java source file was generated by the Gradle 'init' task.
 */

//run command:gradle runInteractive --console=plain

import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;

public class App {
    public static Scanner scanner = new Scanner(System.in);
    public static User loggedInUser = null;

    public static void main(String[] args) {

        ////////////////////////////set up users//////////////////////////////////////
        // database path
        String scrollPath = "src/main/resources/Scrolls.json";
        String userPath = "src/main/resources/User.json";

        // All users in the database
        ArrayList<User> allUsers = JsonManagement.getUsersJson(userPath);

        // All scrolls in the database
        ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson(scrollPath);

        // Add some test users
        // if the user database is empty, add a rootadmin user
        if (allUsers.size() == 0) {
            String rootadminHashedPassword = UserManagement.hashPassword("rootadmin"); // Hash the password before storing it
            RootAdminUser rootadminUser = new RootAdminUser("rootadmin", rootadminHashedPassword, "Root Admin", "rootadmin@example.com", "987654321");
            JsonManagement.writeNewUserJson(userPath, rootadminUser);
            allUsers = JsonManagement.getUsersJson(userPath);
        }


        ////////////////////////////set up scrolls//////////////////////////////////////
        // if the scroll database is empty, add some test scrolls
        if (allScrolls.size() == 0) {
            Scroll scroll1 = new Scroll("123456789", "Root Admin's First Scroll", "rootadmin", "2023/01/01", 2, "0110101", "RootadminFirstScrollpwd123");
            DigitalScrollManager dsm = new DigitalScrollManager(scrollPath);
            dsm.addScroll(scroll1);

            Scroll scroll2 = new Scroll("123456790", "Admin's First Scroll", "admin", "2023/01/03", 100, "110110", "adminFirstScrollpwd111");
            dsm.addScroll(scroll2);
        }

        ////////////////////////////Actual System//////////////////////////////////////
        System.out.println("Welcome to the Virtual Scrolls Access System (VSOS)!");
        while (true) {
            System.out.println("1. Login as a Guest User");
            System.out.println("2. Login as a General User");
            System.out.println("3. Login as an Admin User");
            System.out.println("4. Login as a RootAdmin User");
            System.out.println("5. Register a new General User");
            System.out.println("0. Exit");
            System.out.println("Enter Choice:");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        guestLogin();
                        break;
                    case 2:
                        login();
                        if (loggedInUser != null && getUserType(loggedInUser).equals("General")) {
                            handleLoggedInGeneralUser(scrollPath);
                        }
                        break;
                    case 3:
                        login2();
                        if (loggedInUser != null && getUserType(loggedInUser).equals("Admin")) {
                            handleLoggedInAdminUser();
                        }
                        break;
                    case 4:
                        login3();
                        if (loggedInUser != null && getUserType(loggedInUser).equals("RootAdmin")) {
                            handleLoggedInRootAdminUser();
                        }
                        break;
                    case 5:
                        registerGeneralUser();
                        break;
                    case 0:
                        System.out.println("Exiting the Virtual Scrolls Access System (VSOS).");
                        System.exit(0); // Exit the program
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void handleLoggedInGeneralUser(String ScrollDB) {
        ScrollSeeker ss1 = new ScrollSeeker(ScrollDB);
        ss1.scrollOfTheDay();
        while (true) {
            ScrollSeeker ss = new ScrollSeeker(ScrollDB);

            System.out.println("1. View/Download Scrolls");
            System.out.println("2. Search Scrolls");
            System.out.println("3. Preview Scrolls");
            System.out.println("4. Update User Profile");
            System.out.println("5. View Your Scrolls / Edit Your Scroll(s) Content / Remove Your Scroll(s)");
            System.out.println("6. Upload a new Scroll");
            System.out.println("7. Logout");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        ss.viewAllScrolls();

                        System.out.print("Enter the scroll ID you want to download: ");
                        String scrollID = scanner.nextLine();

                        // Find the Scroll object with the provided scroll ID.
                        ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson(ScrollDB);
                        Scroll scroll = ScrollSeeker.findScrollById(allScrolls, scrollID);

                        if (scroll == null) {
                            System.out.println("Scroll with ID " + scrollID + " not found.");
                        } else {
                            // Invoke the downloadScroll method with the found Scroll object.
                            ScrollSeeker.downloadScroll(scroll);
                        }
                        break;
                    case 2:
                        handleSearch();
                        break;
                    case 3:
                        handlePreviewScroll();
                        break;
                    case 4:
                        loggedInUser.updateProfile();
                        break;
                    case 5:
                        ss.searchByUploaderID(loggedInUser.getCustomIDKey());
                        handleChangeMyScroll();
                        break;
                    case 6:
                        handleUploadScroll();
                        break;
                    case 7:
                        return;

                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void guestLogin() {
        ScrollSeeker ss = new ScrollSeeker("src/main/resources/Scrolls.json");
        while (true){
            System.out.println("Welcome Guest!");
            ss.viewAllScrolls();
            System.out.println("Notice: You are viewing all available scrolls by ScrollID, Name, and Date");
            System.out.println("1. Preview");
            System.out.println("2. Logout");
            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (choice == 1) {
                    handlePreviewScroll();
                } else
                if (choice == 2) {
                    return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void login() {
        System.out.println("Login Page");
        System.out.print("Enter user ID: ");
        String username = scanner.nextLine();
        String password = PasswordField.readPassword("Enter password: ");

        // Validate user credentials and set loggedInUser if successful
        loggedInUser = UserManagement.loginUser(username, password);
        if (loggedInUser != null && getUserType(loggedInUser).equals("General")) {
            System.out.println("Welcome to Scrolls Library!") ;
            System.out.println("Welcome " + loggedInUser.getFullName() + " (UserType: " + getUserType(loggedInUser) + " user)");
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    public static void login2() {
        System.out.println("Login Page");
        System.out.print("Enter user ID: ");
        String username = scanner.nextLine();
        String password = PasswordField.readPassword("Enter password: ");

        // Validate user credentials and set loggedInUser if successful
        loggedInUser = UserManagement.loginUser(username, password);
        if (loggedInUser != null && getUserType(loggedInUser).equals("Admin") && !getUserType(loggedInUser).equals("RootAdmin")) {
            System.out.println("Welcome to Scrolls Library!") ;
            System.out.println("Welcome " + loggedInUser.getFullName() + " (UserType: " + getUserType(loggedInUser) + " user)");
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    public static void login3() {
        System.out.println("Login Page");
        System.out.print("Enter user ID: ");
        String username = scanner.nextLine();
        String password = PasswordField.readPassword("Enter password: ");

        // Validate user credentials and set loggedInUser if successful
        loggedInUser = UserManagement.loginUser(username, password);
        if (loggedInUser != null && getUserType(loggedInUser).equals("RootAdmin")) {
            System.out.println("Welcome to Scrolls Library!") ;
            System.out.println("Welcome " + loggedInUser.getFullName() + " (UserType: " + getUserType(loggedInUser) + " user)");
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    public static void handleLoggedInAdminUser() {
        ScrollSeeker ss1 = new ScrollSeeker("src/main/resources/Scrolls.json");
        ss1.scrollOfTheDay();
        while (true) {
            ScrollSeeker ss = new ScrollSeeker("src/main/resources/Scrolls.json");
            System.out.println("1. View/Download Scrolls");
            System.out.println("2. Search Scrolls");
            System.out.println("3. Preview Scrolls");
            System.out.println("4. Update User Profile");
            System.out.println("5. Admin Functionality");
            System.out.println("6. View Your Scrolls / Edit Your Scroll(s) Content / Remove Scroll(s)");
            System.out.println("7. Upload a new Scroll");
            System.out.println("8. Logout");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        ss.viewAllScrolls();

                        System.out.print("Enter the scroll ID you want to download: ");
                        String scrollID = scanner.nextLine();

                        // Find the Scroll object with the provided scroll ID.

                        Scroll scroll = ScrollSeeker.findScrollById(ScrollSeeker.scrollList, scrollID);

                        if (scroll == null) {
                            System.out.println("Scroll with ID " + scrollID + " not found.");
                        } else {
                            // Invoke the downloadScroll method with the found Scroll object.
                            ScrollSeeker.downloadScroll(scroll);
                        }
                        break;
                    case 2:
                        handleSearch();
                        break;
                    case 3:
                        handlePreviewScroll();
                        break;
                    case 4:
                        loggedInUser.updateProfile();
                        break;
                    case 5:
                        System.out.println("Admin privileges enabled.");
                        handleAdminFuntion();
                        break;
                    case 6:
                        ss.searchByUploaderID(loggedInUser.getCustomIDKey());
                        handleChangeMyScroll();
                        break;
                    case 7:
                        handleUploadScroll();
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void handleAdminFuntion() {

        while (true) {
            System.out.println("1. View all Users Profile");
            System.out.println("2. Add a new General User");
            System.out.println("3. Delete an existing General User");
            System.out.println("4. Grant Admin Privileges to an existing General User");
            System.out.println("5. Add a new Admin User");
            System.out.println("6. Remove General Users' Scroll");
            System.out.println("7. Return to last page");
            System.out.println("0. Exit");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        AdminUser.allUsersProfile();
                        break;
                    case 2:
                        AdminUser.addGeneralUser();
                        break;
                    case 3:
                        AdminUser.deleteGeneralUser();
                        break;
                    case 4:
                        AdminUser.grantPrivilegesByAdmin();
                        break;
                    case 5:
                        AdminUser.addAdminUser();
                        break;
                    case 6:
                        AdminUser.removeUserScroll();
                        break;
                    case 7:
                        return;
                    case 0:
                        System.out.println("Exiting the Virtual Scrolls Access System (VSOS).");
                        System.exit(0); // Exit the program
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void handleRootAdminFuntion() {

        while (true) {
            System.out.println("1. View all Users Profile");
            System.out.println("2. Add a new General User");
            System.out.println("3. Delete an existing General User");
            System.out.println("4. Grant Admin Privileges to an existing General User");
            System.out.println("5. Add a new Admin User");
            System.out.println("6. Delete an existing Admin User");
            System.out.println("7. Remove General/Admin's Scroll");
            System.out.println("8. Return to last page");
            System.out.println("0. Exit");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        RootAdminUser.allUsersProfile();
                        break;
                    case 2:
                        RootAdminUser.addGeneralUser();
                        break;
                    case 3:
                        RootAdminUser.deleteGeneralUser();
                        break;
                    case 4:
                        RootAdminUser.grantPrivilegesByAdmin();
                        break;
                    case 5:
                        RootAdminUser.addAdminUser();
                        break;
                    case 6:
                        RootAdminUser.deleteAdminUser();
                        break;
                    case 7:
                        return;
                    case 0:
                        System.out.println("Exiting the Virtual Scrolls Access System (VSOS).");
                        System.exit(0); // Exit the program
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void handleLoggedInRootAdminUser() {
        ScrollSeeker ss1 = new ScrollSeeker("src/main/resources/Scrolls.json");
        ss1.scrollOfTheDay();
        while (true) {
            ScrollSeeker ss = new ScrollSeeker("src/main/resources/Scrolls.json");
            System.out.println("1. View/Download Scrolls");
            System.out.println("2. Search Scrolls");
            System.out.println("3. Preview Scrolls");
            System.out.println("4. Update User Profile");
            System.out.println("5. Root Admin Functionality");
            System.out.println("6. View Your Scrolls / Edit Your Scroll(s) Content / Remove Scroll(s)");
            System.out.println("7. Upload a new Scroll");
            System.out.println("8. Logout");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        ss.viewAllScrolls();

                        System.out.print("Enter the scroll ID you want to download: ");
                        String scrollID = scanner.nextLine();

                        // Find the Scroll object with the provided scroll ID.

                        Scroll scroll = ScrollSeeker.findScrollById(ScrollSeeker.scrollList, scrollID);

                        if (scroll == null) {
                            System.out.println("Scroll with ID " + scrollID + " not found.");
                        } else {
                            // Invoke the downloadScroll method with the found Scroll object.
                            ScrollSeeker.downloadScroll(scroll);
                        }
                        break;
                    case 2:
                        handleSearch();
                        break;
                    case 3:
                        handlePreviewScroll();
                        break;
                    case 4:
                        loggedInUser.updateProfile();
                        break;
                    case 5:
                        System.out.println("Root Admin privileges enabled.");
                        handleRootAdminFuntion();
                        break;
                    case 6:
                        ss.searchByUploaderID(loggedInUser.getCustomIDKey());
                        handleChangeMyScroll();
                        break;
                    case 7:
                        handleUploadScroll();
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next();
            }
        }
    }

    public static void handleSearch() {
        while (true) {
            System.out.println("1. Search by UploaderID");
            System.out.println("2. Search by Scroll name");
            System.out.println("3. Search by Date");
            System.out.println("4. Search by ScrollID");
            System.out.println("5. Return to the last page");
            System.out.println("Please enter in the format 'choice, condition', e.g. '1, fiona' or '3, 2023/01/01'");
            System.out.println("If you want to return to the last page, please enter '5'.");

            String input = scanner.nextLine();
            String[] parts = input.split(",");

            // if the input is not in the format for options other than 5
            if (parts.length != 2 && !parts[0].trim().equals("5")) {
                System.out.println("Invalid input. Please use the format 'choice, data'.");
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a valid choice.");
                continue;
            }
            ScrollSeeker ss = new ScrollSeeker("src/main/resources/Scrolls.json");
            switch (choice) {
                case 1:
                    String uploaderID = parts[1].trim();
                    ss.searchByUploaderID(uploaderID);
                    break;
                case 2:
                    String scrollName = parts[1].trim();
                    ss.searchByScrollName(scrollName);
                    break;
                case 3:
                    String date = parts[1].trim();
                    ss.searchByScrollUploadDate(date);
                    break;
                case 4:
                    String scrollID = parts[1].trim();
                    ss.searchByScrollID(scrollID);
                    break;
                case 5:
                    System.out.println("Return to the last page");
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public static void handlePreviewScroll() {
        while (true) {
            System.out.println("Please enter the scroll ID you want to preview:");
            String input = scanner.nextLine();
            // check if the input is valid
            if (input.length() != 9) {
                System.out.println("Please enter a valid scroll ID.");
                continue;
            }
            // check if the scroll exists
            ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/main/resources/Scrolls.json");
            boolean scrollExists = false;
            Scroll targetScroll = null;
            for (Scroll scroll : allScrolls) {
                if (scroll.getScrollID().equals(input)) {
                    scrollExists = true;
                    targetScroll = scroll;
                    break;
                }
            }
            if (!scrollExists) {
                System.out.println("No scroll was found with the scroll ID: " + input);
                continue;
            }
            // preview the scroll
            ScrollSeeker ss = new ScrollSeeker("src/main/resources/Scrolls.json");
            ss.previewScroll(targetScroll);
            return;
        }
    }

    public static void handleUploadScroll() {
        while (true) {
            System.out.print("Please enter the name of the scroll you want to upload: ");
            String scrollName = scanner.nextLine();
            System.out.println("Please enter the content of the scroll you want to upload: ");
            String scrollContent = scanner.nextLine();
            // additional feature: password protection for the scroll
            System.out.println("Please enter the password of the scroll you want to upload: ");
            System.out.println("Note: Password must be at least 8 characters long.");
            String scrollPassword = scanner.nextLine();
            // check if the scroll name is valid
            if (scrollName.length() == 0) {
                System.out.println("Please enter a valid scroll name.");
                continue;
            }
            // check if the scroll content is valid
            if (scrollContent.length() == 0) {
                System.out.println("Please enter a valid scroll content.");
                continue;
            }
            // check scroll content if it only contains 0 and 1
            if (!scrollContent.matches("[01]+")) {
                System.out.println("Error: The scroll content contains invalid characters");
                continue;
            }
            // check if the scroll password is valid
            if (scrollPassword.length() == 0) {
                System.out.println("Error: Password cannot be empty");
                continue;
            }

            if (scrollPassword.length() < 8) {
                System.out.println("Error: Password must be at least 8 characters long");
                continue;
            }

            // upload the scroll
            DigitalScrollManager dsm = new DigitalScrollManager("src/main/resources/Scrolls.json");
            Scroll newScroll = new Scroll(scrollName, loggedInUser.getCustomIDKey(), scrollContent, scrollPassword);
            dsm.addScroll(newScroll);
            System.out.println("Scroll uploaded successfully.");
            return;
        }
    }

    public static void handleChangeMyScroll() { // general user
        while (true) {
            System.out.println("\n1. Edit your scroll content by scrollID");
            System.out.println("2. Remove your scroll by scrollID");
            System.out.println("3. Return to the last page");
            System.out.println("Please enter in the format 'choice, scrollID' for choices other than 3, e.g. '1,123456789'");

            String input = scanner.nextLine();
            if (input.length() == 0) {
                System.out.println("Invalid input. Please use the format 'choice, scrollID'.");
                continue;
            }

            if (input.equals("3")) {
                return;
            }
            String[] inputArr = input.split(",");
            // if length is not 2
            if (inputArr.length != 2) {
                System.out.println("Invalid input. Please use the format 'choice, scrollID'.");
                continue;
            }

            int choice;
            try {
                // check if the input is valid
                choice = Integer.parseInt(inputArr[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a valid choice.");
                continue;
            }

            // check if the scroll exists
            ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/main/resources/Scrolls.json");
            boolean scrollExists = false;
            Scroll targetScroll = null;
            // TODO: if logged in user is admin --> if it's also rootadmin
            for (Scroll scroll : allScrolls) {
                if (scroll.getScrollID().equals(inputArr[1]) && scroll.getScrollUploaderID().equals(loggedInUser.getCustomIDKey())) {
                    scrollExists = true;
                    targetScroll = scroll;
                    break;
                }
            }
            if (!scrollExists) {
                System.out.println("No scroll was found with the scroll ID: " + input.split(input, ',')[1]);
                continue;
            }

            // cases
            switch (choice) {
                case 1:
                    System.out.println("Please enter the new content of the scroll you want to edit: ");
                    String newScrollContent = scanner.nextLine();
                    // check if the scroll content is valid
                    if (newScrollContent.length() == 0) {
                        System.out.println("Please enter a valid scroll content.");
                        continue;
                    }
                    // check scroll content if it only contains 0 and 1
                    if (!newScrollContent.matches("[01]+")) {
                        System.out.println("Error: The scroll content contains invalid characters");
                        continue;
                    }
                    // update the scroll content
                    DigitalScrollManager dsm = new DigitalScrollManager("src/main/resources/Scrolls.json");
                    dsm.updateScrollContent(targetScroll, newScrollContent);
                    System.out.println("Scroll content updated successfully.");
                    return;
                case 2:
                    // remove the scroll
                    if ((getUserType(loggedInUser).equals("RootAdmin"))||(getUserType(loggedInUser).equals("Admin"))) {
                        DigitalScrollManager dsm2 = new DigitalScrollManager("src/main/resources/Scrolls.json");
                        dsm2.removeScroll(targetScroll);
                        System.out.println("Scroll removed successfully.");
                        return;
                    }

                    if (getUserType(loggedInUser).equals("General")) {
                        if (targetScroll.getScrollUploaderID().equals(loggedInUser.getCustomIDKey())){
                            DigitalScrollManager dsm2 = new DigitalScrollManager("src/main/resources/Scrolls.json");
                            dsm2.removeScroll(targetScroll);
                            System.out.println("Scroll removed successfully.");
                            return;
                        }
                    }

                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    public static void registerGeneralUser() {
        UserManagement.createUser();
    }


    public static String getUserType(User user) {
        if (user instanceof GeneralUser) {
            return "General";
        } else if (user instanceof RootAdminUser) {
            return "RootAdmin";
        } else {
            return "Admin";
        } 
    }
}
