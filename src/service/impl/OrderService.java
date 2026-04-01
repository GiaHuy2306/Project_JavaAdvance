package service.impl;

import dao.IMenuItemDAO;
import dao.IOrderDAO;
import dao.IOrderItemDAO;
import dao.impl.MenuItemDAO;
import dao.impl.OrderDAO;
import dao.impl.OrderItemDAO;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.enums.OrderItemStatus;
import model.enums.OrderStatus;
import utils.DBConnection;
import service.IOrderService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService implements IOrderService {

    private final IOrderDAO orderDAO = new OrderDAO();
    private final IOrderItemDAO orderItemDAO = new OrderItemDAO();
    private final IMenuItemDAO menuItemDAO = new MenuItemDAO();

    @Override
    public Order createOrder(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setTableId(tableId);
            order.setStatus(OrderStatus.PENDING);
            order = orderDAO.save(conn, order);
            conn.commit();
            return order;
        } catch (SQLException e) {
            throw new Exception("Tạo order thất bại: " + e.getMessage());
        }
    }

    @Override
    public Order getActiveByTableAndCustomer(int tableId, int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Order order = orderDAO.findActiveByTableAndCustomer(conn, tableId, customerId);

            if (order == null) {
                throw new Exception("Bạn không có đơn hàng nào đang hoạt động tại bàn này.");
            }

            if (order != null && order.getStatus().equals(OrderStatus.DELETE)) {
                return null;
            }
            return  order;
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể truy xuất thông tin đơn hàng lúc này");
        }
    }

    @Override
    public List<Order> getActiveOrdersByCustomer(int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            return orderDAO.findActiveByCustomer(conn, customerId);
        } catch (SQLException e) {
            throw new Exception("Của bạn không có Order này");
        }
    }

    @Override
    public Order getActiveOrderByTable(int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Order order = orderDAO.findActiveByTable(conn, tableId);

            if (order == null) {
                throw new Exception("Bàn này hiện chưa có đơn hàng nào đang hoạt động.");
            }
            return order;
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể truy xuất thông tin đơn hàng của bàn này.");
        }
    }

    @Override
    public void checkoutByTable(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Lấy thông tin Order hiện tại
                Order order = orderDAO.findActiveByTableAndCustomer(conn, tableId, customerId);
                if (order == null) throw new Exception("Không tìm thấy đơn hàng nào để thanh toán!");

                // 2. Lấy danh sách món ăn của Order này
                List<OrderItem> items = orderItemDAO.findByOrderId(conn, order.getId());

                double totalAmount = 0;
                for (OrderItem item : items) {
                    // Nếu món chưa được phục vụ xong và cũng không bị hủy -> Chặn lại
                    if (item.getStatus() != OrderItemStatus.SERVED && item.getStatus() != OrderItemStatus.CANCEL) {
                        MenuItem menu = menuItemDAO.findById(conn, item.getMenuId());
                        throw new Exception("Không thể thanh toán! Món [" + menu.getName() + "] đang ở trạng thái " + item.getStatus() + ".");
                    }

                    // Chỉ tính tiền các món đã được phục vụ (SERVED)
                    if (item.getStatus() == OrderItemStatus.SERVED) {
                        totalAmount += item.getPriceAtOrder() * item.getQuantity();
                    }
                }

                // 3. Cập nhật tổng tiền cuối cùng vào Database
                orderDAO.updateTotal(conn, order.getId(), totalAmount);

                // 4. Chuyển trạng thái Order sang DONE và lưu thời gian checkout
                boolean success = orderDAO.checkout(conn, order.getId());
                if (!success) throw new Exception("Hệ thống không thể cập nhật trạng thái đơn hàng.");

                conn.commit();
            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Quá trình thanh toán gặp sự cố kỹ thuật.");
        }
    }
}