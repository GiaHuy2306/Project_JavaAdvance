package dao.impl;

import dao.IOrderItemDAO;
import model.OrderItem;
import model.enums.OrderItemStatus;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements IOrderItemDAO {

    @Override
    public void save(OrderItem item) throws Exception {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getMenuId());
            ps.setInt(3, item.getQuantity());
            ps.setString(4, item.getStatus().name());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Thêm món thất bại!");

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) item.setId(rs.getInt(1));
        }
    }

    @Override
    public List<OrderItem> findByOrder(int orderId) throws Exception {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapToOrderItem(rs));
            }
        }
        return list;
    }

    @Override
    public OrderItem findById(int id) throws Exception {
        String sql = "SELECT * FROM order_items WHERE order_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapToOrderItem(rs);
        }
        return null;
    }

    @Override
    public void updateStatus(int orderItemId, OrderItemStatus status) throws Exception {
        String sql = "UPDATE order_items SET status = ? WHERE order_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, orderItemId);
            ps.executeUpdate();
        }
    }

    private OrderItem mapToOrderItem(ResultSet rs) throws SQLException {
        return new OrderItem(
                rs.getInt("order_item_id"),
                rs.getInt("order_id"),
                rs.getInt("menu_id"),
                rs.getInt("quantity"),
                rs.getDouble("price_at_order"),
                OrderItemStatus.fromString(rs.getString("status"))
        );
    }
}