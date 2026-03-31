package presentation.customer;

import dto.OrderItemView;
import model.MenuItem;
import model.Order;
import model.Table;
import model.enums.OrderStatus;
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
            List<Table> tables = tableService.findStatusTable(TableStatus.EMPTY);

            if (tables.isEmpty()) {
                System.out.println("Không có bàn nào trống!");
                return;
            }

            System.out.println("\n===== DANH SÁCH BÀN =====");
            System.out.printf("%-5s %-15s %-10s%n", "STT", "Tên", "Trạng thái");
            for (int i = 0; i < tables.size(); i++) {
                Table t = tables.get(i);
                System.out.printf("%-5d %-15s %-10s%n", i + 1, t.getName(), t.getStatus());
            }

            int choice;
            while (true) {
                choice = InputMethod.inputInt("Chọn bàn: ");
                if (choice >= 1 && choice <= tables.size()) break;
                System.out.println("Lựa chọn không hợp lệ, hãy chọn từ 1 đến " + tables.size() + "!");
            }

            int tableId = tables.get(choice - 1).getId();
            orderService.createOrder(userId, tableId);
            System.out.println("Đã chọn bàn " + tables.get(choice - 1).getName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= GỌI MÓN =================
    private void addItem(int userId) throws Exception {
        int tableId = chooseTable(userId);
        if (tableId == -1) return;

        Order order = orderService.getActiveOrderByTableAndCustomer(tableId, userId);
        if (order == null || !(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.IN_PROGRESS)) {
            System.out.println("Bàn chưa có order hoặc không được phép gọi món!");
            return;
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

        orderItemService.addItemByTable(userId, tableId, selectedItem.getId(), quantity);
        System.out.println("Gọi món thành công!");
    }

    // ================= XEM MÓN =================
    private void viewItems(int userId) {
        int tableId = chooseTable(userId);
        if (tableId == -1) return;

        showItems(userId, tableId);
    }

    // ================= HỦY MÓN =================
    private void cancelItem(int userId) throws Exception {
        int tableId = chooseTable(userId);
        if (tableId == -1) return;

        List<OrderItemView> list = orderItemService.getByTable(userId, tableId);
        if (list.isEmpty()) {
            System.out.println("Không có món để hủy");
            return;
        }

        showItems(userId, tableId);

        int choice;
        while (true) {
            choice = InputMethod.inputInt("Nhập ID món cần hủy: ");
            if (choice >= 1 && choice <= list.size()) break;
            System.out.println("Lựa chọn không hợp lệ!");
        }

        int itemId = list.get(choice - 1).getId();
        orderItemService.cancelItem(userId, itemId);
        System.out.println("Hủy thành công!");
    }

    // ================= THANH TOÁN =================
    private void checkout(int userId) throws Exception {
        int tableId = chooseTable(userId);
        if (tableId == -1) return;

        orderService.checkoutByTable(userId, tableId);
        System.out.println("Thanh toán thành công!");
    }

    // ================= HELPER =================
    private int chooseTable(int userId) {
        try {
            List<Order> orders = orderService.getActiveOrdersByCustomer(userId);
            if (orders.isEmpty()) {
                System.out.println("Bạn không có bàn nào");
                return -1;
            }

            System.out.println("\n===== BÀN CỦA BẠN =====");
            System.out.printf("%-5s %-10s %-10s%n", "STT", "Table ID", "Trạng thái");
            for (int i = 0; i < orders.size(); i++) {
                System.out.printf("%-5d %-10s %-10s%n",
                        i + 1, orders.get(i).getTableId(), orders.get(i).getStatus());
            }

            int choice = InputMethod.inputInt("Chọn bàn: ");
            if (choice < 1 || choice > orders.size()) {
                System.out.println("Lựa chọn không hợp lệ");
                return -1;
            }

            return orders.get(choice - 1).getTableId();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private void showItems(int userId, int tableId) {
        try {
            List<OrderItemView> list = orderItemService.getByTable(userId, tableId);
            if (list.isEmpty()) {
                System.out.println("Chưa có món");
                return;
            }

            System.out.println("\n===== DANH SÁCH MÓN =====");
            System.out.printf("%-5s %-20s %-10s %-10s %-10s%n",
                    "ID", "Tên món", "SL", "Giá", "Trạng thái");

            double total = 0;
            for (OrderItemView i : list) {
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
}