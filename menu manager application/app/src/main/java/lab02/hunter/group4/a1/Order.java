package lab02.hunter.group4.a1;

import java.time.LocalDate;
import java.util.HashMap;


public class Order {

    private String orderNo;
    private LocalDate orderDate;
    private HashMap<String, Integer> orderItems; // item name, quantity
    private double totalPrice;

    public Order(String orderNo, LocalDate orderDate, HashMap<String, Integer> orderItems, double totalPrice) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }
    
    public String getOrderNo() {
        return orderNo;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public HashMap<String, Integer> getOrderItems() {
        return orderItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void orderToString() {
        System.out.println("Order No: " + orderNo);
        System.out.println("Order Date: " + orderDate);
        for (String item : orderItems.keySet()) {
            System.out.println("Item: " + item + " Quantity: " + orderItems.get(item));
        }
        System.out.println("Total Price: " + totalPrice);
    }

}
