package dao;

import dto.OrderItemView;
import model.OrderItem;
import model.enums.OrderItemStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IOrderItemDAO {

    void save(OrderItem item) throws Exception;

    List<OrderItem> findByOrder(int orderId) throws Exception;

    OrderItem findById(int id) throws Exception;

    void updateStatus(int orderItemId, OrderItemStatus status) throws Exception;
}
