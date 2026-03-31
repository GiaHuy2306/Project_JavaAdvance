package service.impl;

import dao.IOrderItemDAO;
import dao.impl.OrderItemDAO;
import dao.impl.MenuItemDAO;
import dto.OrderItemView;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.enums.OrderItemStatus;
import service.IOrderItemService;
import service.IOrderService;
import utils.DBConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class OrderItemService implements IOrderItemService {

    private final IOrderItemDAO orderItemDAO = new OrderItemDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final IOrderService orderService = new OrderService();

    @Override
    public void addItemByTable(int customerId, int tableId, int menuItemId, int quantity) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Order order = orderService.getActiveByTableAndCustomer(tableId, customerId);
            if(order == null) throw new Exception("Bàn chưa có order");

            MenuItem item = menuItemDAO.findById(conn, menuItemId);
            if(item == null) throw new Exception("Món không tồn tại");
            if(item.getStock() < quantity) throw new Exception("Không đủ stock");

            // trừ stock
            menuItemDAO.updateStock(conn, menuItemId, item.getStock() - quantity);

            // thêm order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setMenuId(menuItemId);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtOrder(item.getPrice());
            orderItem.setStatus(OrderItemStatus.PENDING);

            orderItemDAO.save(conn, orderItem);

            conn.commit();
        } catch (Exception e) {
            throw new Exception("Thêm món thất bại: " + e.getMessage());
        }
    }

    @Override
    public List<OrderItemView> getByTable(int customerId, int tableId) throws Exception {
        Order order = orderService.getActiveByTableAndCustomer(tableId, customerId);
        if(order == null) return new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            List<OrderItem> items = orderItemDAO.findByOrderId(conn, order.getId());
            List<OrderItemView> views = new ArrayList<>();
            for(OrderItem i : items) {
                MenuItem m = menuItemDAO.findById(conn, i.getMenuId());
                views.add(new OrderItemView(
                        i.getId(),
                        i.getOrderId(),
                        m != null ? m.getName() : null,
                        i.getPriceAtOrder(),
                        i.getQuantity(),
                        i.getStatus().name()
                ));
            }
            return views;
        }
    }

    @Override
    public void cancelItem(int customerId, int orderItemId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            OrderItem item = orderItemDAO.findById(conn, orderItemId);
            if(item == null) throw new Exception("Không tìm thấy món");
            if(item.getStatus() == OrderItemStatus.SERVED) throw new Exception("Món đã được phục vụ, không thể hủy");

            MenuItem menu = menuItemDAO.findById(conn, item.getMenuId());
            if(menu != null) {
                menuItemDAO.updateStock(conn, menu.getId(), menu.getStock() + item.getQuantity());
            }

            orderItemDAO.updateStatus(conn, orderItemId, OrderItemStatus.CANCEL.name());
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Hủy món thất bại: " + e.getMessage());
        }
    }
}