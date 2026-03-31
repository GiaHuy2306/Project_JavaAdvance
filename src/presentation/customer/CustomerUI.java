package presentation.customer;

import dto.OrderItemView;
import dto.TableChoice;
import model.MenuItem;
import model.Order;
import model.Table;
import model.enums.TableStatus;
import service.IMenuItemService;
import service.IOrderItemService;
import service.IOrderService;
import service.ITableService;
import service.impl.MenuItemService;
import service.impl.OrderItemService;
import service.impl.OrderService;
import service.impl.TableService;
import utils.InputMethod;

import java.util.List;

public class CustomerUI {

    private final IOrderService orderService = new OrderService();
    private final IOrderItemService orderItemService = new OrderItemService();
    private final ITableService tableService = new TableService();
    private final IMenuItemService menuItemService = new MenuItemService();

    public boolean menu(int userId) {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Chọn bàn");
            System.out.println("2. Gọi món");
            System.out.println("3. Xem món đã gọi");
            System.out.println("4. Hủy món");
            System.out.println("5. Thanh toán");
            System.out.println("0. Thoát");

            int choice = InputMethod.inputInt("Chọn: ");

            try {
                switch (choice) {
                    case 1 -> selectTable(userId);
                    case 2 -> addItem(userId);
                    case 3 -> viewItems(userId);
                    case 4 -> cancelItem(userId);
                    case 5 -> checkout(userId);
                    case 0 -> {
                        System.out.println("Thoát!");
                        return true;
                    }
                    default -> System.out.println("Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ================= CHỌN BÀN =================
    private void selectTable(int userId) {
        try {
            List<Table> tables = tableService.findStatusTable(TableStatus.EMPTY); // chỉ bàn EMPTY
            if (tables.isEmpty()) {
                System.out.println("Không có bàn nào trống!");
                return;
            }

            System.out.println("\n===== DANH SÁCH BÀN =====");
            System.out.printf("%-5s %-15s %-10s%n", "STT", "Tên bàn", "Trạng thái");
            for (int i = 0; i < tables.size(); i++) {
                Table t = tables.get(i);
                System.out.printf("%-5d %-15s %-10s%n",
                        i + 1, t.getName(), t.getStatus());
            }

            int choice;
            while (true) {
                choice = InputMethod.inputInt("Chọn bàn: ");
                if (choice >= 1 && choice <= tables.size()) break;
                System.out.println("Lựa chọn không hợp lệ, hãy chọn từ 1 đến " + tables.size());
            }

            int tableId = tables.get(choice - 1).getId();

            // Tạo order nếu chưa có
            if (orderService.getActiveByTableAndCustomer(tableId, userId) == null) {
                orderService.createOrder(userId, tableId);
            }

            System.out.println("Đã chọn bàn " + tables.get(choice - 1).getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= GỌI MÓN =================
    private void addItem(int userId) {
        TableChoice table = chooseTableWithName(userId);
        if (table == null) return;

        try {
            // Tạo order nếu chưa có
            if (orderService.getActiveByTableAndCustomer(table.getTableId(), userId) == null) {
                orderService.createOrder(userId, table.getTableId());
            }

            List<MenuItem> menuList = menuItemService.findAll();
            if (menuList.isEmpty()) {
                System.out.println("Không có món nào!");
                return;
            }

            System.out.println("\n===== MENU =====");
            System.out.printf("%-5s %-20s %-10s %-10s%n", "STT", "Tên", "Giá", "Stock");
            for (int i = 0; i < menuList.size(); i++) {
                MenuItem m = menuList.get(i);
                System.out.printf("%-5d %-20s %-10.2f (còn %d)%n",
                        i + 1, m.getName(), m.getPrice(), m.getStock());
            }

            int choice;
            while (true) {
                choice = InputMethod.inputInt("Chọn món: ");
                if (choice >= 1 && choice <= menuList.size()) break;
                System.out.println("Lựa chọn không hợp lệ!");
            }

            MenuItem selectedItem = menuList.get(choice - 1);

            int quantity;
            while (true) {
                quantity = InputMethod.inputInt("Nhập số lượng: ");
                if (quantity > 0) break;
                System.out.println("Số lượng phải > 0");
            }

            orderItemService.addItemByTable(userId, table.getTableId(), selectedItem.getId(), quantity);
            System.out.println("Gọi món thành công cho bàn " + table.getTableName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= XEM MÓN =================
    private void viewItems(int userId) {
        TableChoice table = chooseTableWithName(userId);
        if (table == null) return;
        showItems(userId, table.getTableId());
    }

    // ================= HỦY MÓN =================
    private void cancelItem(int userId) {
        TableChoice table = chooseTableWithName(userId);
        if (table == null) return;

        try {
            List<OrderItemView> items = orderItemService.getByTable(userId, table.getTableId());
            if (items.isEmpty()) {
                System.out.println("Không có món để hủy");
                return;
            }

            showItems(userId, table.getTableId());

            int choice = InputMethod.inputInt("Nhập ID món cần hủy: ");
            boolean valid = items.stream().anyMatch(i -> i.getId() == choice);
            if (!valid) {
                System.out.println("Lựa chọn không hợp lệ!");
                return;
            }

            orderItemService.cancelItem(userId, choice);
            System.out.println("Hủy thành công!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= THANH TOÁN =================
    private void checkout(int userId) {
        TableChoice table = chooseTableWithName(userId);
        if (table == null) return;

        try {
            orderService.checkoutByTable(userId, table.getTableId());
            System.out.println("Thanh toán thành công cho bàn " + table.getTableName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= HELPER =================
    private TableChoice chooseTableWithName(int userId) {
        try {
            List<Order> orders = orderService.getActiveOrdersByCustomer(userId);
            if (orders.isEmpty()) {
                System.out.println("Bạn không có bàn nào");
                return null;
            }

            System.out.println("\n===== BÀN CỦA BẠN =====");
            System.out.printf("%-5s %-10s %-20s %-10s%n", "STT", "Table ID", "Tên bàn", "Trạng thái");

            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                Table t = tableService.findById(o.getTableId());
                if (t == null || t.getStatus() == TableStatus.DELETED) continue; // skip bàn DELETED
                System.out.printf("%-5d %-10d %-20s %-10s%n",
                        i + 1, o.getTableId(),
                        t.getName(),
                        o.getStatus());
            }

            int choice = InputMethod.inputInt("Chọn bàn: ");
            if (choice < 1 || choice > orders.size()) {
                System.out.println("Lựa chọn không hợp lệ!");
                return null;
            }

            Order o = orders.get(choice - 1);
            Table t = tableService.findById(o.getTableId());
            if (t == null || t.getStatus() == TableStatus.DELETED) {
                System.out.println("Bàn không tồn tại hoặc đã bị xóa!");
                return null;
            }

            return new TableChoice(o.getTableId(), t.getName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void showItems(int userId, int tableId) {
        try {
            List<OrderItemView> items = orderItemService.getByTable(userId, tableId);
            if (items.isEmpty()) {
                System.out.println("Chưa có món");
                return;
            }

            System.out.println("\n===== DANH SÁCH MÓN =====");
            System.out.printf("%-5s %-20s %-10s %-10s %-10s%n",
                    "ID", "Tên món", "SL", "Giá", "Trạng thái");

            double total = 0;
            for (OrderItemView i : items) {
                String name = i.getName() != null ? i.getName() : "[Đã xóa]";
                double price = i.getPriceAtOrder();
                total += price * i.getQuantity();
                System.out.printf("%-5d %-20s %-10d %-10.2f %-10s%n",
                        i.getId(), name, i.getQuantity(), price, i.getStatus());
            }

            System.out.println("---------------------------------------");
            System.out.printf("TỔNG TIỀN: %.2f%n", total);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper class lưu ID và tên bàn
//    private static class TableChoice {
//        int tableId;
//        String tableName;
//        public TableChoice(int tableId, String tableName) {
//            this.tableId = tableId;
//            this.tableName = tableName;
//        }
//    }
}