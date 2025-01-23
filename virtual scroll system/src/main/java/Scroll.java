
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Scroll {
    // Each scroll will have a unique name and ID
    private String scrollID;
    private String scrollName;
    private String scrollUploaderID;
    private String scrollUploadDate;
    private int noOfDownloads;
    private String scrollContent;
    private String scrollPassword;

    public Scroll(String scrollName, String scrollUploaderID, String scrollContent, String scrollPassword) {
        this(null, scrollName, scrollUploaderID, null, 0, scrollContent, scrollPassword);
    }

    public Scroll(String scrollID, String scrollName, String scrollUploaderID, String scrollUploadDate, int noOfDownloads, String scrollContent, String scrollPassword) {
        // if scrollID is not given, meaning a new scroll is uploaded by a user
        if (scrollID == null) {
            this.scrollID = setScrollID();
        }
        else {
            this.scrollID = scrollID;
        }
        this.scrollName = scrollName;
        this.scrollUploaderID = scrollUploaderID;
        // if scrollUploadDate is not given, meaning a new scroll is uploaded by a user at the current date
        if (scrollUploadDate == null) {
            this.scrollUploadDate = setScrollUploadDate();
        }
        else {
            this.scrollUploadDate = scrollUploadDate;
        }
        this.noOfDownloads = noOfDownloads;
        this.scrollContent = setScrollContent(scrollContent);
        this.scrollPassword = scrollPassword;
    }

    public String getScrollID() {
        return scrollID;
    }

    public String setScrollID() {
        // Generate a unique random 9-digit number
        while (true) {
            Random rand = new Random();
            int scrollId = 100000000 + rand.nextInt(900000000);
            String scrollIdString = Integer.toString(scrollId);
            // check if the scrollID already exists i.e. scrollID must be unique
            if (ScrollSeeker.searchByScrollID(scrollIdString) == null) {
                return scrollIdString;
            }
        }
    }

    public String getScrollName() {
        return scrollName;
    }

    public String getScrollUploaderID() {
        return scrollUploaderID;
    }

    public String getScrollUploadDate() {
        return scrollUploadDate;
    }

    public String setScrollUploadDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public int setNoOfDownloads() {
        return noOfDownloads++;
    }

    public int getNoOfDownloads() {
        return noOfDownloads;
    }

    public String setScrollContent(String scrollContent) {
        // error checking if the file exists and if the file only contains 1 and 0
        try {
            if (scrollContent.matches("[01]+")) {
                this.scrollContent = scrollContent;
                return scrollContent;
            }
            else {
                System.out.println("Error: The file contains characters other than 1 and 0");
                return null;
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    public String getScrollContent() {
        return scrollContent;
    }

    public String getScrollPassword() {
        return scrollPassword;
    }
}