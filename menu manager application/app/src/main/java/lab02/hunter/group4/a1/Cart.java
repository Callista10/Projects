package lab02.hunter.group4.a1;

import java.text.SimpleDateFormat;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class Cart {
    HashMap<String, Integer> cartItems;
    JsonManagement jsonManager;
    Scanner scanner;

    public Cart() {
        cartItems = new HashMap<>();
        jsonManager = new JsonManagement();
        scanner = new Scanner(System.in);
    }

    public void add_item() {
        while (true) {
            System.out.print("Enter the name of the dish you want to add: ");
            String itemName = scanner.nextLine();

            HashMap<String, JSONArray> menu = jsonManager.getMenuInfo(jsonManager.MENU_FILE);
            boolean itemExists = false;
            for (String category : menu.keySet()) {
                JSONArray items = menu.get(category);
                for (Object itemObj : items) {
                    JSONObject item = (JSONObject) itemObj;
                    if (item.get("name").equals(itemName)) {
                        itemExists = true;
                        System.out.print("Enter the quantity: ");
                        int quantity = scanner.nextInt();
                        cartItems.put(itemName, quantity);
                        scanner.nextLine(); // Consume the newline character
                        break;
                    }
                }
                if (itemExists) {
                    break;
                }
            }

            if (!itemExists) {
                System.out.println("Item not found in the menu. Please enter a valid item name.");
                continue;
            }

            System.out.print("Do you want to add more items to the cart? (yes/no): ");
            String moreItems = scanner.nextLine();
            if (moreItems.equalsIgnoreCase("no")) {
                break;
            }
        }
    }

    public void adjust_quantity() {
        System.out.print("Enter the name of the dish you want to adjust: ");
        String itemName = scanner.nextLine();

        if (cartItems.containsKey(itemName)) {
            System.out.print("Enter the new quantity: ");
            int newQuantity = scanner.nextInt();
            cartItems.put(itemName, newQuantity);
        } else {
            System.out.println("Item not found in the cart.");
        }
    }

    public void remove_items_cart() {
        System.out.print("Enter the name of the dish you want to remove: ");
        String itemName = scanner.nextLine();

        if (cartItems.containsKey(itemName)) {
            System.out.println(itemName + " has been removed from the cart.");
            cartItems.remove(itemName);
        } else {
            System.out.println("Item not found in the cart.");
        }
    }

    public void confirm_order() {
        System.out.print("Choose Delivery or Pickup: ");
        String deliveryOption = scanner.nextLine();

        // Calculate total amount based on cart items and menu prices
        double totalAmount = 0;
        HashMap<String, JSONArray> menu = jsonManager.getMenuInfo(jsonManager.MENU_FILE);
        for (String itemName : cartItems.keySet()) {
            for (String category : menu.keySet()) {
                JSONArray items = menu.get(category);
                for (Object itemObj : items) {
                    JSONObject item = (JSONObject) itemObj;
                    if (item.get("name").equals(itemName)) {
                        double price = Double.parseDouble(item.get("price").toString());
                        totalAmount += price * cartItems.get(itemName);
                        break;
                    }
                }
            }
        }

        // Generate order number and update order.json
        String orderNumber = generateOrderNumber();
        jsonManager.addNewOrderToJson(new Order(orderNumber, getCurrentDate(), cartItems, totalAmount), jsonManager.ORDER_FILE);

        // Display final order information
        System.out.println("Order Number: " + orderNumber);
        System.out.println("Items: " + cartItems.keySet());
        System.out.println("Total Amount: $" + totalAmount);
        System.out.println("Delivery Option: " + deliveryOption);
        System.out.println("Date: " + getCurrentDate());
    }

    private String generateOrderNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        return "ORD" + timestamp;
    }

    private LocalDate getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(LocalDate.now().toString(), formatter);
    }

    public void Cstart() {
        Boolean loop = true;
        // Menu loop
        while (loop) {
            System.out.println("1. Add Item");
            System.out.println("2. Adjust Quantity");
            System.out.println("3. Remove Items from Cart");
            System.out.println("4. Confirm Order");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    add_item();
                    break;
                case 2:
                    adjust_quantity();
                    break;
                case 3:
                    remove_items_cart();
                    break;
                case 4:
                    confirm_order();
                    System.exit(0);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

}






