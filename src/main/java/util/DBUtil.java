package util;


import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static DataSource dataSource;

    static {
        MysqlConnectionPoolDataSource mysql = new MysqlConnectionPoolDataSource();
        mysql.setServerName("localhost");
        mysql.setPort(3306);
        mysql.setUser("root");
        mysql.setPassword("123456");
        mysql.setDatabaseName("cash");
        mysql.setUseSSL(false);
        mysql.setCharacterEncoding("utf8");

        dataSource = mysql;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if(resultSet != null) {
                resultSet.close();
            }
            if(preparedStatement != null) {
                preparedStatement.close();
            }
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
