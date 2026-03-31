package service.impl;

import dao.IOrderItemDAO;
import dao.impl.OrderItemDAO;
import dao.impl.MenuItemDAO;
import dto.ChefItemView;
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

            List<OrderItem> existingItems = orderItemDAO.findByOrderId(conn, order.getId());
            OrderItem existingItem = null;

            for (OrderItem i : existingItems) {
                // Chỉ cộng dồn nếu cùng món và trạng thái là PENDING (chưa phục vụ)
                if (i.getMenuId() == menuItemId && i.getStatus() == OrderItemStatus.PENDING) {
                    existingItem = i;
                    break;
                }
            }

            if (existingItem != null) {
                // TRƯỜNG HỢP 1: Đã có món -> Cập nhật số lượng mới
                int newQuantity = existingItem.getQuantity() + quantity;
                orderItemDAO.updateQuantity(conn, existingItem.getId(), newQuantity);
            } else {
                // TRƯỜNG HỢP 2: Món mới hoàn toàn -> Thêm mới vào DB
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(order.getId());
                newItem.setMenuId(menuItemId);
                newItem.setQuantity(quantity);
                newItem.setPriceAtOrder(item.getPrice());
                newItem.setStatus(OrderItemStatus.PENDING);

                orderItemDAO.save(conn, newItem);
            }

            // trừ stock
            menuItemDAO.updateStock(conn, menuItemId, item.getStock() - quantity);

//            // thêm order item
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrderId(order.getId());
//            orderItem.setMenuId(menuItemId);
//            orderItem.setQuantity(quantity);
//            orderItem.setPriceAtOrder(item.getPrice());
//            orderItem.setStatus(OrderItemStatus.PENDING);

//            orderItemDAO.save(conn, orderItem);

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

            if(item.getStatus() == OrderItemStatus.COOKING) {
                throw new Exception("Món ăn đang được chế biến, vui lòng liên hệ nhân viên để hủy.");
            }

            MenuItem menu = menuItemDAO.findById(conn, item.getMenuId());
            if(menu != null) {
                menuItemDAO.updateStock(conn, menu.getId(), menu.getStock() + item.getQuantity());
            }

            orderItemDAO.updateStatus(conn, orderItemId, OrderItemStatus.CANCEL.name());
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống: Không thể thực hiện lệnh hủy món lúc này");
        }
    }

    @Override
    public List<ChefItemView> getChefPendingItems() throws Exception {
        // Tự động mở connection và gọi DAO để lấy dữ liệu
        try (Connection conn = DBConnection.getConnection()) {
            return orderItemDAO.getPendingAndCookingItems(conn);
        } catch (Exception e) {
            throw new Exception("Lỗi khi tải danh sách bếp: " + e.getMessage());
        }
    }

    @Override
    public void advanceItemStatus(int orderItemId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            OrderItem item = orderItemDAO.findById(conn, orderItemId);
            if (item == null) throw new Exception("Không tìm thấy món ăn với ID này!");

            // Lấy type của món để check logic FOOD/DRINK
            MenuItem menu = menuItemDAO.findById(conn, item.getMenuId());
            boolean isDrink = menu != null && "DRINK".equalsIgnoreCase(menu.getFoodType().name());

            OrderItemStatus currentStatus = item.getStatus();
            OrderItemStatus nextStatus = null;

            // LOGIC CHUYỂN TRẠNG THÁI
            switch (currentStatus) {
                case PENDING:
                    // Nếu là đồ uống -> Bỏ qua COOKING, lên thẳng READY
                    nextStatus = isDrink ? OrderItemStatus.READY : OrderItemStatus.COOKING;
                    break;
                case COOKING:
                    nextStatus = OrderItemStatus.READY;
                    break;
                case READY:
                    nextStatus = OrderItemStatus.SERVED;
                    break;
                case SERVED:
                    throw new Exception("Món này đã được phục vụ xong rồi!");
                case CANCEL:
                    throw new Exception("Món này đã bị khách hủy!");
            }

            // Cập nhật trạng thái mới
            orderItemDAO.updateStatus(conn, orderItemId, nextStatus.name());

        } catch (Exception e) {
            throw new Exception("Cập nhật trạng thái thất bại: " + e.getMessage());
        }
    }
}