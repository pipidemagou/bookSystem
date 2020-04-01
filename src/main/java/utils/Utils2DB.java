package utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//JDBC连接池工具类
public class Utils2DB {
    //数据库连接池的数据源对象
    private static ComboPooledDataSource comboPooledDataSource = null;

    //静态初始化
    static {
        comboPooledDataSource = new ComboPooledDataSource();
    }

    //获取数据库连接池的数据源对象
    public static DataSource getDataSource() {

        return comboPooledDataSource;
    }

    //获取数据源的连接对象
    public static Connection connection() {
        try {
            return comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库初始化失败了！");
        }
    }
}
