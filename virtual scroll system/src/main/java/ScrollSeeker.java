import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;




public class ScrollSeeker {

    static ArrayList<Scroll> scrollList = new ArrayList<Scroll>();

    public ScrollSeeker(String database_path) {
        ScrollSeeker.scrollList = JsonManagement.getScrollsJson(database_path);
    }

    // View all available scrolls (list of title names, icons, etc.)
    public static void viewAllScrolls() {
        // Error checking when no scrolls are available to view
        if (scrollList == null || scrollList.isEmpty() || scrollList.size() == 0 || scrollList.get(0) == null) {
            System.out.println("No scrolls are available.");
            return;
        }
        System.out.println("================AVAILABLE SCROLLS================");
        for (Scroll scroll : scrollList) {
            System.out.println("--------------------------------------------------" +
                    "\nScroll ID: " + scroll.getScrollID() +
                    "\nScroll Name: " + scroll.getScrollName() +
                    "\nScroll Uploader ID: " + scroll.getScrollUploaderID() +
                    "\nScroll Upload Date: " + scroll.getScrollUploadDate() +
                    "\nNo. of Downloads: " + scroll.getNoOfDownloads());
        }
        System.out.println("==================================================");
    }
    
    public static Scroll findScrollById(ArrayList<Scroll> scrollList, String scrollID) {
        for (Scroll scroll : scrollList) {
            if (scroll.getScrollID().equals(scrollID)) {
                return scroll;
            }
        }
        return null; // Scroll not found.
    }
    
    // Download a scroll
    public static void downloadScroll(Scroll scroll) {

        if (scroll == null) {
            System.out.println("Invalid scroll provided.");
            return;
        }
        // Increase the number of downloads for the scroll.
        scroll.setNoOfDownloads();
        // update to the database
        DigitalScrollManager dsm = new DigitalScrollManager("src/main/resources/Scrolls.json");
        dsm.removeScroll(scroll);
        dsm.addScroll(scroll);

        // Save the scroll content to a .txt file.
        String fileName = scroll.getScrollID() + "_" + scroll.getScrollName() + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(scroll.getScrollContent());
            System.out.println("Scroll " + scroll.getScrollName() + " has been downloaded and saved as " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the scroll: " + e.getMessage());
        }
    }

    // Search filters: by uploader ID, scroll ID, name and upload date
    public static ArrayList<Scroll> searchByUploaderID(String uploaderID) {
        ArrayList<Scroll> searchResults = new ArrayList<Scroll>();
        ArrayList<String> allUploaderIDs = new ArrayList<String>();
        // Error checking when uploaderID is null, invalid, etc.
        if (uploaderID == null) {
            System.out.println("Please enter a valid uploader ID.");
            return null;
        }

        // TODO: when user enters unrealistically long or short uploaderID

        else {
            // case insensitive search
            for (Scroll scroll : scrollList) {
                allUploaderIDs.add(scroll.getScrollUploaderID());
                if (scroll.getScrollUploaderID().toLowerCase().equals(uploaderID.toLowerCase())) {
                    searchResults.add(scroll);
                }
            }
            if (allUploaderIDs.isEmpty()) {
                System.out.println("No scrolls are available.");
                return null;
            }

            if (searchResults.isEmpty()) {
                System.out.println("No scrolls were found with the uploader ID: " + uploaderID);
                return null;
            }

            else {
                System.out.println("================SEARCH RESULTS FOR UPLOADER ID: " + uploaderID + " ================");
                for (Scroll scroll : searchResults) {
                    System.out.println("--------------------------------------------------" +
                            "\nUploader ID: " + scroll.getScrollUploaderID() +
                            "\nScroll ID: " + scroll.getScrollID() +
                            "\nScroll Name: " + scroll.getScrollName() +
                            "\nScroll Upload Date: " + scroll.getScrollUploadDate() +
                            "\nNo. of Downloads: " + scroll.getNoOfDownloads());
                }
                System.out.println("==================================================");
                return searchResults;
            }
        }
    }

    public static ArrayList<Scroll> searchByScrollID(String scrollID) {
        ArrayList<Scroll> searchResults = new ArrayList<Scroll>();
        // Error checking when scrollID is null, invalid, etc.
        if (scrollID == null) {
            System.out.println("Please enter a valid scroll ID.");
            return null;
        }

        // Error checking when the scrollID is not of length 9
        if (scrollID.length() != 9) {
            System.out.println("Please enter a valid scroll ID.");
            return null;
        }

        for (Scroll scroll : scrollList) {
            if (scroll.getScrollID().equals(scrollID)) {
                searchResults.add(scroll);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No scrolls were found with the scroll ID: " + scrollID);
            return null;
        }

        else {
            System.out.println("================SEARCH RESULTS FOR SCROLL ID: " + scrollID + " ================");
            for (Scroll scroll : searchResults) {
                System.out.println("--------------------------------------------------" +
                        "\nScroll Name: " + scroll.getScrollName() +
                        "\nScroll Uploader ID: " + scroll.getScrollUploaderID() +
                        "\nScroll Upload Date: " + scroll.getScrollUploadDate() +
                        "\nNo. of Downloads: " + scroll.getNoOfDownloads());
            }
            System.out.println("==================================================");
            return searchResults;
        }
    }

    public static ArrayList<Scroll> searchByScrollName(String scrollName) {
        ArrayList<Scroll> searchResults = new ArrayList<Scroll>();
        // Error checking when scrollName is null, invalid, etc.
        if (scrollName == null) {
            System.out.println("Please enter a valid scroll name.");
            return null;
        }
        // TODO: error checking when scrollName is too long or short

        // case insensitive search
        for (Scroll scroll : scrollList) {
            if (scroll.getScrollName().toLowerCase().equals(scrollName.toLowerCase())) {
                searchResults.add(scroll);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No scrolls were found with the scroll name: " + scrollName);
            return null;
        }

        else {
            System.out.println("================SEARCH RESULTS FOR SCROLL NAME: '" + scrollName + "' ================");
            for (Scroll scroll : searchResults) {
                System.out.println("--------------------------------------------------" +
                        "\nScroll ID: " + scroll.getScrollID() +
                        "\nScroll Uploader ID: " + scroll.getScrollUploaderID() +
                        "\nScroll Upload Date: " + scroll.getScrollUploadDate() +
                        "\nNo. of Downloads: " + scroll.getNoOfDownloads());
            }
            System.out.println("==================================================");
            return searchResults;
        }
    }

    public static ArrayList<Scroll> searchByScrollUploadDate(String scrollUploadDate) {
        // Error checking whether the given scrollUploadDate is null, wrong format, etc.
        if (scrollUploadDate == null) {
            System.out.println("Please enter a valid scroll upload date.");
            return null;
        }

        try {
            DateTimeFormatter scrollUploadDateDTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            scrollUploadDateDTF.parse(scrollUploadDate);
        } catch (Exception e) {
            System.out.println("Please enter a valid scroll upload date in the format: yyyy/MM/dd");
            return null;
        }

        // // Error checking when the scrollUploadDate is not of length 10
        // if (scrollUploadDate.length() != 10) {
        //     System.out.println("Please enter a valid scroll upload date.");
        //     return null;
        // }

        ArrayList<Scroll> searchResults = new ArrayList<Scroll>();
        // search based on scroll upload date
        for (Scroll scroll : scrollList) {
            if (scroll.getScrollUploadDate().equals(scrollUploadDate)) {
                searchResults.add(scroll);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No scrolls were found with the scroll upload date: " + scrollUploadDate);
            return null;
        }

        else {
            System.out.println("================SEARCH RESULTS FOR SCROLL UPLOAD DATE: " + scrollUploadDate + " ================");
            for (Scroll scroll : searchResults) {
                System.out.println("--------------------------------------------------" +
                        "\nScroll ID: " + scroll.getScrollID() +
                        "\nScroll Name: " + scroll.getScrollName() +
                        "\nScroll Uploader ID: " + scroll.getScrollUploaderID() +
                        "\nNo. of Downloads: " + scroll.getNoOfDownloads());
            }
            System.out.println("==================================================");
            return searchResults;
        }
    }

    // Preview Scrolls: 100 characters of the scroll
    public static void previewScroll(Scroll scroll) {
        // output the first 30% of the scroll content and make the rest denoted by *
        String scrollContent = scroll.getScrollContent();
        int scrollContentLength = scrollContent.length();
        int previewLength = (int) Math.ceil(scrollContentLength * 0.3);
        String preview = scrollContent.substring(0, previewLength);
        for (int i = previewLength; i < scrollContentLength; i++) {
            preview += "*";
        }
        System.out.println("========================Preview for Scroll ID: " + scroll.getScrollID() + "========================");
        System.out.println(preview);
        System.out.println("==================================================================================================");
    }

    // Scroll of the day: randomly select a scroll from the database and display the information
    public static void scrollOfTheDay() {
        // error checking when there are no scrolls in the database
        if (scrollList == null || scrollList.isEmpty() || scrollList.size() == 0 || scrollList.get(0) == null) {
            System.out.println("No scrolls are available.");
            return;
        }
        // random int between 0 and scrollList.size() exclusive of the upper bound
        int randomIndex = new Random().nextInt(scrollList.size());
        
        Scroll scrollOfTheDay = scrollList.get(randomIndex);
        System.out.println("========================Scroll of the Day========================" +
                "\nScroll ID: " + scrollOfTheDay.getScrollID() +
                "\nScroll Name: " + scrollOfTheDay.getScrollName() +
                "\nScroll Uploader ID: " + scrollOfTheDay.getScrollUploaderID() +
                "\nScroll Upload Date: " + scrollOfTheDay.getScrollUploadDate() +
                "\nNo. of Downloads: " + scrollOfTheDay.getNoOfDownloads());
        System.out.println("=================================================================");
    }

    // public static void main(String[] args) {
    //     ArrayList<Scroll> allScrolls = JsonManagement.getScrollsJson("src/main/resources/Scrolls.json");
    //     ScrollSeeker scrollSeeker = new ScrollSeeker("src/main/resources/Scrolls.json");
    //     scrollOfTheDay();
    // }
}
