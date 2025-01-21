package lab02.hunter.group4.a1;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class OrderHistory {
    private Boolean is_admin;
    private String orderPath;
    private ArrayList<Order> orderHistory;

    public OrderHistory(Boolean is_admin, String orderPath){
        this.is_admin = is_admin;
        JsonManagement jsonManager = new JsonManagement();
        this.orderHistory = jsonManager.getOrderHistory(orderPath);
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public void display_order_history(){
        if(is_admin){
            for(Order order: orderHistory) {
//                System.out.println(order.getOrderNo().toString()+ order.getOrderDate().toString()+ order.getOrderItems().toString()+
//                                   order.getTotalPrice());
                order.orderToString();
                System.out.println();
            }
        }
        else{
            System.out.println("Permission Denied! Only admins can access this information.");
        }
    }

    public void display_total_order() {
        if (is_admin) {
            int totalOrder = orderHistory.size();
            System.out.println("Total Order: " + totalOrder);
        } else {
            System.out.println("Permission Denied! Only admins can access this information.");
        }
    }

    public Order get_targeted_order(String order_number){
        if (is_admin) {
            for (Order order : orderHistory) {
                if (order.getOrderNo().equals(order_number)) {
                    return order;
                }
            }
            System.out.println("Order not found!");
        } else {
            System.out.println("Permission Denied! Only admins can access this information.");
        }
        return null;
    }


}
