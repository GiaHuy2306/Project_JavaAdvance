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

import java.text.DecimalFormat;
import java.util.List;

public class CustomerUI {

    private final IOrderService orderService = new OrderService();
    private final IOrderItemService orderItemService = new OrderItemService();
    private final ITableService tableService = new TableService();
    private final IMenuItemService menuItemService = new MenuItemService();

    public boolean menu(int userId) {
        while (true) {
            String separator = "+-----------------------------------+";

            System.out.println("\n" + separator);
            System.out.println("|           CUSTOMER MENU           |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. Chọn bàn");
            System.out.printf("| %-33s |\n", "2. Gọi món");
            System.out.printf("| %-33s |\n", "3. Xem món đã gọi");
            System.out.printf("| %-33s |\n", "4. Hủy món");
            System.out.printf("| %-33s |\n", "5. Thanh toán");
            System.out.printf("| %-33s |\n", "0. Đăng xuất");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn: ");

            try {
                switch (choice) {
                    case 1 -> selectTable(userId);
                    case 2 -> addItem(userId);
                    case 3 -> viewItems(userId);
                    case 4 -> cancelItem(userId);
                    case 5 -> checkout(userId);
                    case 0 -> {
                        boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                        if (confirm){
                            return true;
                        }
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

            String separator = "+-------+-----------------+-----------------+";

            System.out.println("\n" + separator);
            System.out.println("|              DANH SÁCH BÀN                |");
            System.out.println(separator);
            System.out.printf("| %-5s | %-15s | %-15s |%n", "STT", "Tên bàn", "Trạng thái");
            System.out.println(separator);

            for (int i = 0; i < tables.size(); i++) {
                Table t = tables.get(i);
                System.out.printf("| %-5d | %-15s | %-15s |%n",
                        i + 1, t.getName(), t.getStatus());
            }
            System.out.println(separator);

            int choice;
            while (true) {
                choice = InputMethod.inputInt("Chọn bàn: ");
                if (choice >= 1 && choice <= tables.size()) break;
                System.out.println("Lựa chọn không hợp lệ, hãy chọn từ 1 đến " + tables.size());
            }

            Table selectedTable = tables.get(choice - 1);

            // Tạo order nếu chưa có
            if (orderService.getActiveByTableAndCustomer(selectedTable.getId(), userId) == null) {
                orderService.createOrder(userId, selectedTable.getId());
            }

            selectedTable.setStatus(TableStatus.FULL);
            tableService.updateTable(selectedTable);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= GỌI MÓN =================
    private void addItem(int userId) {
        TableChoice choice = chooseTableWithName(userId);
        if (choice == null) return;

        try {
            // Lấy order hiện tại
            Order order = orderService.getActiveByTableAndCustomer(choice.getTableId(), userId);
            if (order == null) {
                System.out.println("Bàn chưa có order, tạo order mới...");
                orderService.createOrder(userId, choice.getTableId());
                order = orderService.getActiveByTableAndCustomer(choice.getTableId(), userId);
            }

            List<MenuItem> menuList = menuItemService.findAll();
            if (menuList.isEmpty()) {
                System.out.println("Không có món nào!");
                return;
            }

            String separator = "+-------+---------------------------+-----------------+------------+";

            System.out.println("\n" + separator);
            System.out.println("|                               MENU                                |");
            System.out.println(separator);

            System.out.printf("| %-5s | %-25s | %-15s | %-11s |%n", "STT", "Tên món", "Giá", "Kho (Stock)");
            System.out.println(separator);

            DecimalFormat df = new DecimalFormat("#,### VNĐ");
            for (int i = 0; i < menuList.size(); i++) {
                MenuItem m = menuList.get(i);
                String stockInfo = "Còn " + m.getStock();

                System.out.printf("| %-5d | %-25s | %-15s | %-11s |%n",
                        i + 1,
                        m.getName(),
                        df.format(m.getPrice()),
                        stockInfo);
            }
            System.out.println(separator);

            int menuChoice;
            while (true) {
                menuChoice = InputMethod.inputInt("Chọn món: ");
                if (menuChoice >= 1 && menuChoice <= menuList.size()) break;
                System.out.println("Lựa chọn không hợp lệ!");
            }

            MenuItem selectedItem = menuList.get(menuChoice - 1);

            int quantity;
            while (true) {
                quantity = InputMethod.inputInt("Nhập số lượng: ");
                if (quantity > 0) break;
                System.out.println("Số lượng phải > 0");
            }

            orderItemService.addItemByTable(userId, choice.getTableId(), selectedItem.getId(), quantity);
            System.out.println("Gọi món thành công cho bàn " + choice.getTableName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= XEM MÓN =================
    private void viewItems(int userId) {
        TableChoice choice = chooseTableWithName(userId);
        if (choice == null) return;
        showItems(userId, choice.getTableId());
    }

    // ================= HỦY MÓN =================
    private void cancelItem(int userId) {
        TableChoice choice = chooseTableWithName(userId);
        if (choice == null) return;

        try {
            List<OrderItemView> items = orderItemService.getByTable(userId, choice.getTableId());
            if (items.isEmpty()) {
                System.out.println("Không có món để hủy");
                return;
            }

            showItems(userId, choice.getTableId());

            int itemChoice = InputMethod.inputInt("Nhập ID món cần hủy: ");
            boolean valid = items.stream().anyMatch(i -> i.getId() == itemChoice);
            if (!valid) {
                System.out.println("Lựa chọn không hợp lệ!");
                return;
            }

            orderItemService.cancelItem(userId, itemChoice);
            System.out.println("Hủy thành công!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= THANH TOÁN =================
    private void checkout(int userId) {
        TableChoice choice = chooseTableWithName(userId);
        if (choice == null) return;

        try {
            orderService.checkoutByTable(userId, choice.getTableId());

            Table table = tableService.findById(choice.getTableId());
            if (table != null){
                table.setStatus(TableStatus.EMPTY);
                tableService.updateTable(table);
            }

            System.out.println("Thanh toán thành công cho bàn: " + choice.getTableName());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= HELPER =================
    private TableChoice chooseTableWithName(int userId) {
        try {
            List<Order> activeOrders = orderService.getActiveOrdersByCustomer(userId);

            // 1. TẠO DANH SÁCH CHỈ CHỨA BÀN HỢP LỆ
            List<Order> validOrders = new java.util.ArrayList<>();
            for (Order order : activeOrders) {
                if (tableService.findById(order.getTableId()) != null) {
                    validOrders.add(order);
                }
            }

            if (validOrders.isEmpty()) {
                System.out.println("Bạn chưa có bàn nào!, Vui lòng chọn bàn trước");
                return null;
            }

            String separator = "+-------+----------------------+-----------------+";

            System.out.println("\n" + separator);
            System.out.println("|                  BÀN CỦA BẠN                    |");
            System.out.println(separator);

            System.out.printf("| %-5s | %-20s | %-16s |%n", "STT", "Tên bàn", "Trạng thái Order");
            System.out.println(separator);

            for (int i = 0; i < validOrders.size(); i++) {
                Order order = validOrders.get(i);
                Table t = tableService.findById(order.getTableId());

                System.out.printf("| %-5d | %-20s | %-16s |%n",
                        i + 1,
                        t.getName(),
                        order.getStatus());
            }
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn bàn: ");
            if (choice < 1 || choice > validOrders.size()) {
                System.out.println("Lựa chọn không hợp lệ!");
                return null;
            }

            Order selectedOrder = validOrders.get(choice - 1);
            Table t = tableService.findById(selectedOrder.getTableId());

            return new TableChoice(selectedOrder.getTableId(), t.getName());

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

            String separator = "+-------+---------------------------+-------+-----------------+-----------------+";

            System.out.println("\n" + separator);

            System.out.println("|                                DANH SÁCH MÓN                                  |");
            System.out.println(separator);

            System.out.printf("| %-5s | %-25s | %-5s | %-15s | %-15s |%n",
                    "ID", "Tên món", "SL", "Giá", "Trạng thái");
            System.out.println(separator);

            double total = 0;
            DecimalFormat df = new DecimalFormat("#,### VNĐ");

            for (OrderItemView i : items) {
                String name = i.getName() != null ? i.getName() : "[Đã xóa]";
                double price = i.getPriceAtOrder();

                if (!"CANCEL".equalsIgnoreCase(i.getStatus())) {
                    total += price * i.getQuantity();
                }

                // In từng dòng dữ liệu của món ăn
                System.out.printf("| %-5d | %-25s | %-5d | %-15s | %-15s |%n",
                        i.getId(),
                        name,
                        i.getQuantity(),
                        df.format(price),
                        i.getStatus());
            }

            System.out.println(separator);

            String totalLine = String.format("TỔNG TIỀN (KHÔNG TÍNH MÓN HỦY): %s", df.format(total));
            System.out.printf("| %-77s |%n", totalLine);
            System.out.println(separator);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}