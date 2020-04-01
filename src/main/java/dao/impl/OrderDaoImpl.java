package dao.impl;

import dao.OrderDao;
import domain.Order;
import domain.OrderItem;
import domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import utils.Utils2DB;


import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by ozc on 2017/4/17.
 */
public class OrderDaoImpl implements OrderDao {

    @Override
    public void addOrder(Order order) {
        QueryRunner queryRunner = new QueryRunner(Utils2DB.getDataSource());
        String sql1 = "INSERT INTO orders(id,date,user_id,state,price) VALUES(?,?,?,?,?)";
        try {
            //订单的基本信息
            queryRunner.update(sql1, new Object[]{order.getId(), order.getDate(), order.getUser_id(), order.isState(), order.getPrice()});

            //订单项的信息
            String sql2 = "INSERT INTO orderItem(id,price,quantity,order_id,book_id) VALUES(?,?,?,?,?)";

            Set<OrderItem> items = order.getItems();

            for (OrderItem item : items) {
                queryRunner.update(sql2, new Object[]{item.getId(), item.getPrice(), item.getQuantity(), item.getOrder_id(), item.getBook_id()});
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order findOrder(String id) {
        QueryRunner queryRunner = new QueryRunner(Utils2DB.getDataSource());

        Order order;
        try {
            //找出订单的基本信息
            String sql = "SELECT * FROM orders WHERE id=?";
            order = (Order) queryRunner.query(sql, new BeanHandler(Order.class), new Object[]{id});

            //找出订单的所有订单项
            String sql2 = "SELECT * FROM orderItem WHERE order_id=?";
            List<OrderItem> list = (List<OrderItem>) queryRunner.query(sql2, new BeanListHandler(OrderItem.class), new Object[]{order.getId()});



            //将所有订单项装到订单里边
            order.getItems().addAll(list);


            //找出该订单是属于哪一个用户的
            String sql3 = "SELECT * FROM orders o,user u WHERE o.user_id=u.id AND o.id=? ";
            User user = (User) queryRunner.query(sql3, new BeanHandler(User.class), new Object[]{order.getId()});

            order.setUser_id(user.getId());
            return order;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //更新订单的状态
    public void updateState(String id) {

        QueryRunner queryRunner = new QueryRunner(Utils2DB.getDataSource());

        String sql = "UPDATE orders SET state=? WHERE id=?";

        try {
            queryRunner.update(sql, new Object[]{true, id});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //查看已经发货或没发货的订单信息
    public List<Order> getAllOrder(boolean state) {
        QueryRunner queryRunner = new QueryRunner(Utils2DB.getDataSource());

        String sql = "SELECT * FROM orders WHERE state=? ";
        try {
            return (List<Order>) queryRunner.query(sql, new BeanListHandler(Order.class), new Object[]{state});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //通过用户的id查找用户的订单,可能是不止一个的
    public List<Order> findUserOrder(String user_id) {
        QueryRunner queryRunner = new QueryRunner(Utils2DB.getDataSource());

        String sql = "SELECT * FROM orders WHERE user_id=? ";

        try {
            return (List<Order>) queryRunner.query(sql, new BeanListHandler(Order.class), new Object[]{user_id});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
