package common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum OrderStatus {
    PAYING(1,"待支付"),OK(2,"支付完成");
    private int flag;
    private String desc;

    OrderStatus(int flag,String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public static OrderStatus valueOf(int flag) {
        for (OrderStatus orderStatus:OrderStatus.values()) {
            if(orderStatus.flag == flag) {
                return orderStatus;
            }
        }
        throw new RuntimeException("OrderStatus is not found");
    }

    public int getFlg() {
        return this.flag;
    }
}
