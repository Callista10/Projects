package lab02.hunter.group4.a1;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonManagement {

    protected final static String ADMIN_FILE = "src/main/resources/admin.json";
    protected final static String MENU_FILE = "src/main/resources/menu.json";
    protected final static String ORDER_FILE = "src/main/resources/order.json";

    // read json file and return a object - helper function
    public Object readJson(String fileName) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            obj = parser.parse(fileReader);
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    // helper function
    public JSONArray getAdminJson(String admin_path) {
        JSONArray admin = (JSONArray) readJson(admin_path);
        return admin;
    }

    // return a hashmap of admin info in the format of {username: password}
    public HashMap<String, String> getAdminInfo(String admin_path) {
        JSONArray admin = this.getAdminJson(admin_path);
        HashMap<String, String> adminInfo = new HashMap<String, String>();
        for (int i = 0; i < admin.size(); i++) {
            JSONObject adminObj = (JSONObject) admin.get(i);
            String username = (String) adminObj.get("username");
            String password = (String) adminObj.get("password");
            adminInfo.put(username, password);
        }
        return adminInfo;
    }

    // write or update to json file
    public void updateJSON(String file_path, Object full_content) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(file_path), full_content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // add new admin info into json file
    public void addAdmin(String username, String password, String admin_path) {
        JSONArray adminArray = this.getAdminJson(admin_path);
        JSONObject newAdmin = new JSONObject();
        newAdmin.put("username", username);
        newAdmin.put("password", password);
        adminArray.add(newAdmin);
        updateJSON(admin_path, adminArray);
    }

    // return a hashmap of menu info in the format of {category: [item1, item2, ...]}
    public HashMap<String, JSONArray> getMenuInfo(String menu_path) {
        JSONObject menu = (JSONObject) readJson(menu_path);
        HashMap<String, JSONArray> menuInfo = new HashMap<String, JSONArray>();
        for (Object key : menu.keySet()) {
            String category = (String) key;
            JSONArray items = (JSONArray) menu.get(category);
            menuInfo.put(category, items);
        }
        return menuInfo;
    }

    // return an arraylist of order objects
    public ArrayList<Order> getOrderHistory(String order_path) {
        JSONObject orderJson = (JSONObject) readJson(order_path);
        JSONArray allOrders = (JSONArray) orderJson.get("orders");
        ArrayList<Order> orderList = new ArrayList<Order>();
        for (int o = 0; o < allOrders.size(); o++) {
            JSONObject orderObj = (JSONObject) allOrders.get(o);
            String orderNo = (String) orderObj.get("orderNumber");
            String orderDate = (String) orderObj.get("orderDate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(orderDate, formatter);
            JSONArray items = (JSONArray) orderObj.get("items");
            HashMap<String, Integer> orderItems = new HashMap<String, Integer>();
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                String itemName = (String) item.get("name");
                int quantity = ((Long) item.get("quantity")).intValue();
                orderItems.put(itemName, quantity);
            }
            double totalPrice = (double) orderObj.get("total");
            orderList.add(new Order(orderNo, date, orderItems, totalPrice));
        }
        return orderList;
    }

    // append new order to order.json
    public void addNewOrderToJson(Order newOrder, String order_path) {
        JSONObject orderJson = (JSONObject) readJson(order_path);
        JSONArray allOrders = (JSONArray) orderJson.get("orders");
        JSONObject orderObj = new JSONObject();
        orderObj.put("orderNumber", newOrder.getOrderNo());
        orderObj.put("orderDate", newOrder.getOrderDate().toString());
        JSONArray items = new JSONArray();
        for (String itemName : newOrder.getOrderItems().keySet()) {
            JSONObject item = new JSONObject();
            item.put("name", itemName);
            item.put("quantity", newOrder.getOrderItems().get(itemName));
            items.add(item);
        }
        orderObj.put("items", items);
        orderObj.put("total", newOrder.getTotalPrice());
        allOrders.add(orderObj);
        orderJson.put("orders", allOrders);
        updateJSON(order_path, orderJson);
    }



    // // for testing
    // public static void main(String[] args) {
    //     JsonManagement jsonManagement = new JsonManagement();
    //     HashMap<String, String> admin = jsonManagement.getAdminInfo();
    //     System.out.println(admin);

    //     jsonManagement.addAdmin("testAdmin", "testAdminpwd");
    //     admin = jsonManagement.getAdminInfo();
    //     System.out.println(admin);

    //     HashMap<String, JSONArray> menu = jsonManagement.getMenuInfo();
    //     menu.keySet();
    //     System.out.println(menu.keySet());

    //     ArrayList<Order> orderHistory = jsonManagement.getOrderHistory();
    //     System.out.println(orderHistory);
    // }
}