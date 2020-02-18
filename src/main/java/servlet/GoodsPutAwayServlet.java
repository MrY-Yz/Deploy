package servlet;

import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
@WebServlet("/inbound")
public class GoodsPutAwayServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf8");
        resp.setContentType("text/html; charset=utf8");
        resp.setCharacterEncoding("utf8");

        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        String stock = req.getParameter("stock");
        String unit = req.getParameter("unit");
        String price = (req.getParameter("price"));
        double doublePrice = Double.valueOf(price);
        System.out.println(doublePrice);
        int realPrice = (int)(doublePrice*100);
        String discount = req.getParameter("discount");

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String sql = "insert into goods(name,introduce,stock,unit,price,discount) values(?,?,?,?,?,?)";
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,introduce);
            preparedStatement.setInt(3,Integer.valueOf(stock));
            preparedStatement.setString(4,unit);
            preparedStatement.setInt(5, realPrice);
            preparedStatement.setInt(6,Integer.valueOf(discount));
            preparedStatement.executeUpdate();
            resp.sendRedirect("index.html");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
    }
}
