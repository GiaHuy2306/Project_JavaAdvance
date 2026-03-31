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
    public OrderItem save(Connection conn, OrderItem item) throws Exception {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity, price_at_order, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getMenuId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPriceAtOrder());
            ps.setString(5, item.getStatus().name());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) item.setId(rs.getInt(1));
            return item;
        }
    }

    @Override
    public List<OrderItem> findByOrderId(Connection conn, int orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public OrderItem findById(Connection conn, int itemId) {
        String sql = "SELECT * FROM order_items WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean updateQuantity(Connection conn, int itemId, int quantity) {
        String sql = "UPDATE order_items SET quantity=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updateStatus(Connection conn, int itemId, String status) {
        String sql = "UPDATE order_items SET status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(Connection conn, int itemId) {
        String sql = "DELETE FROM order_items WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private OrderItem map(ResultSet rs) throws SQLException {
        return new OrderItem(
                rs.getInt("order_item_id"),
                rs.getInt("order_id"),
                rs.getInt("menu_id"),
                rs.getInt("quantity"),
                rs.getDouble("price_at_order"),
                OrderItemStatus.valueOf(rs.getString("status"))
        );
    }
}