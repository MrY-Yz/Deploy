package servlet;

import common.OrderStatus;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyGoodsServlet")
public class BuyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Order order =(Order) session.getAttribute("order");
        List<Goods> goodsList = (List<Goods>)session.getAttribute("list");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setFinish_time(LocalDateTime.now().format(formatter));
        order.setOrder_status(OrderStatus.OK);

        boolean effect = this.commitOrder(order);
        if(effect) {
            for(Goods goods : goodsList) {
                boolean isUpdate = updateAfterBuy(goods,goods.getBuyGoodsNum());
                if(isUpdate) {
                    System.out.println("更新库成功");
                } else {
                    System.out.println("更新库失败");
                }
            }
            resp.sendRedirect("buyGoodsSuccess.html");

        }
    }

    private boolean commitOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            String insertOrder = "insert into `order`(id,account_id,account_name,create_time, " +
                    " finish_time, actual_amount,total_money,order_status) " +
                    " values (?,? ,?,now(),now(),?,?,?)";
            String insertOrderItem = "insert into `order_item`(order_id,goods_id, " +
                    " goods_name,goods_introduce,goods_num,goods_unit, " +
                    " goods_price,goods_discount) values (?,?,?,?,?,?,?,?)";
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(insertOrder);
            preparedStatement.setString(1, order.getId());
            preparedStatement.setInt(2, order.getAccount_id());
            preparedStatement.setString(3, order.getAccount_name());
            preparedStatement.setInt(4, order.getActualAmountInt());
            preparedStatement.setInt(5, order.getTotalMoneyInt());
            preparedStatement.setInt(6, order.getOrder_statusOrderStatus().getFlg());
            if (preparedStatement.executeUpdate() == 0) {
                throw new RuntimeException("插入订单失败");
            }
            preparedStatement = connection.prepareStatement(insertOrderItem);
            for (OrderItem orderItem : order.orderItemList) {
                preparedStatement.setString(1, orderItem.getOrderId());
                preparedStatement.setInt(2, orderItem.getGoodsId());
                preparedStatement.setString(3, orderItem.getGoodsName());
                preparedStatement.setString(4, orderItem.getGoodsIntroduce());
                preparedStatement.setInt(5, orderItem.getGoodsNum());
                preparedStatement.setString(6, orderItem.getGoodsUnit());
                preparedStatement.setInt(7, orderItem.getGoodsPriceInt());
                preparedStatement.setInt(8, orderItem.getGoodsDiscount());
                //将每一项preparedStatement 缓存
                preparedStatement.addBatch();
            }
            //将每一项批量插入
            int[] effects = preparedStatement.executeBatch();
            for (int i : effects) {
                if (i == 0) {
                    throw new RuntimeException("插入订单项失败!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }
        return true;
    }
    private boolean updateAfterBuy(Goods goods,int goodsBuyNum){
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        boolean effect=false;
        try{
            String sql="update goods set stock=? where id=?";
            connection=DBUtil.getConnection();
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,goods.getStock()-goodsBuyNum);
            preparedStatement.setInt(2,goods.getId());
            if(preparedStatement.executeUpdate()==1){
                effect=true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return effect;
    }
}
