package servlet;

import common.OrderStatus;
import entity.Account;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/pay")
public class ReadyBuyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=utf8");

        String goodsIdAndNum = req.getParameter("goodsIdAndNum");
        String[] strings = goodsIdAndNum.split(",");
        List<Goods> list = new ArrayList<>();
        for (String s:strings) {
            String[] strings1 = s.split("-");
            Goods goods = getGoods(Integer.valueOf(strings1[0]));
            if(goods != null) {
                list.add(goods);
                goods.setBuyGoodsNum(Integer.valueOf(strings1[1]));
            }
        }
        System.out.println("当前需要购买的商品"+list);
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");
        //生成订单
        Order order = new Order();
        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        order.setAccount_name(account.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setCreate_time(LocalDateTime.now().format(formatter));
        //每个个货物都是一个订单项OrderItem
        int totalMoney = 0;
        int actualMoney = 0;
        for (Goods goods : list) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setGoodsId(goods.getId());
            orderItem.setGoodsName(goods.getName());
            orderItem.setGoodsIntroduce(goods.getIntroduce());
            orderItem.setGoodsNum(goods.getBuyGoodsNum());
            orderItem.setGoodsUnit(goods.getUnit());
            orderItem.setGoodsPrice(goods.getPriceInt());
            orderItem.setGoodsDiscount(goods.getDiscount());
            order.orderItemList.add(orderItem);

            int curMoney = goods.getBuyGoodsNum() * goods.getPriceInt();
            totalMoney += curMoney;
            actualMoney += curMoney*goods.getDiscount()/100;
        }
        order.setTotal_money(totalMoney);
        order.setActual_amount(actualMoney);
        order.setOrder_status(OrderStatus.PAYING);
        HttpSession session1 = req.getSession();
        session1.setAttribute("order",order);
        session1.setAttribute("list",list);

        resp.getWriter().println("<html>");
        resp.getWriter().println("<p>"+"【用户名称】:"+order.getAccount_name()+"</p>");
        resp.getWriter().println("<p>"+"【订单编号】:"+order.getId()+"</p>");
        //????
        resp.getWriter().println("<p>"+"【订单状态】:"+order.getOrder_statusOrderStatus().getDesc()+"</p>");
        resp.getWriter().println("<p>"+"【创建时间】:"+order.getCreate_time()+"</p>");
        resp.getWriter().println("<p>"+"编号"+"名称"+"数量"+"单位"+"单价(元)"+"</p>");
        resp.getWriter().println("<ol>");
        for(OrderItem orderItem : order.orderItemList){
            resp.getWriter().println("<li>"+orderItem.getGoodsName()+" "+orderItem.getGoodsNum()+
                    "   "+orderItem.getGoodsUnit()+" "+orderItem.getGoodsPrice()*orderItem.getGoodsNum()+"</li>");
        }
        resp.getWriter().println("</ol>");
        resp.getWriter().println("<p>"+"【总金额】:"+order.getTotal_money()+"</p>");
        resp.getWriter().println("<p>"+"【优惠金额】:"+order.getDiscount()+"</p>");
        resp.getWriter().println("<p>"+"【应支付金额】:"+order.getActual_amount()+"</p>");
        resp.getWriter().println("<a href=\"buyGoodsServlet\">确认</a>");
        resp.getWriter().println("<a href= \"index.html\">取消</a>");
        resp.getWriter().println("</html>");
    }

    private Goods getGoods(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Goods goods = null;
        try {
            String sql = "select * from goods where id=?";
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                goods = this.parse(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return goods;
    }

    private Goods parse(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        return goods;
    }
}
