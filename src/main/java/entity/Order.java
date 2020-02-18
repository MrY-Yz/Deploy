package entity;

import common.OrderStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_money;
    private OrderStatus order_status;

    //订单项内容也需存储到订单中
    public List<OrderItem> orderItemList = new ArrayList<>();

    public String getOrder_status() {
        return order_status.getDesc();
    }
    public OrderStatus getOrder_statusOrderStatus() {
        return order_status;
    }

    public double getTotal_money() {
        return total_money*1.0/100;
    }
    public int getTotalMoneyInt() {
        return total_money;
    }
    public double getActual_amount() {
        return actual_amount*1.0/100;
    }
    public int getActualAmountInt() {
        return actual_amount;
    }

    public double getDiscount() {
        return ((getTotalMoneyInt() - getActualAmountInt())*1.0/100);
    }
}
