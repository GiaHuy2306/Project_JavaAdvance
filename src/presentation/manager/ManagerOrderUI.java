package presentation.manager;

import dto.OrderItemView;
import model.Table;
import model.enums.TableStatus;
import service.IOrderItemService;
import service.ITableService;
import service.impl.OrderItemService;
import service.impl.TableService;
import utils.InputMethod;
import java.util.List;

public class ManagerOrderUI {
    private final ITableService tableService = new TableService();
    private final IOrderItemService orderItemService = new OrderItemService();

    public void menu() {
        while (true) {
            String separator = "+-----------------------------------+";
            System.out.println("\n" + separator);
            System.out.println("|       QUẢN LÝ PHỤC VỤ BÀN         |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. Danh sách bàn đang có khách");
            System.out.printf("| %-33s |\n", "2. Duyệt món theo bàn");
            System.out.printf("| %-33s |\n", "0. Quay lại");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn: ");
            switch (choice) {
                case 1 -> showActiveTables();
                case 2 -> approveByTable();
                case 0 -> {
                    boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                    if (confirm){
                        System.out.println("Bạn đã quay lại thành công");
                        return;
                    }
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showActiveTables() {
        try {
            // Lấy các bàn không trống (đang ngồi hoặc đang phục vụ)
            List<Table> tables = tableService.findAll(); // Hoặc lọc theo status != EMPTY
            String separator = "+-------+-----------------+-----------------+";
            System.out.println("\n" + separator);
            System.out.println("|           TRẠNG THÁI CÁC BÀN              |");
            System.out.println(separator);
            System.out.printf("| %-5s | %-15s | %-15s |%n", "ID", "Tên bàn", "Trạng thái");
            System.out.println(separator);

            for (Table t : tables) {
                if (t.getStatus() != TableStatus.EMPTY) {
                    System.out.printf("| %-5d | %-15s | %-15s |%n", t.getId(), t.getName(), t.getStatus());
                }
            }
            System.out.println(separator);
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void approveByTable() {
        try {
            int tableId = InputMethod.inputInt("Nhập ID bàn muốn kiểm duyệt: ");

            List<OrderItemView> items = orderItemService.getByTableForManager(tableId);

            if (items.isEmpty()) {
                System.out.println("Bàn này hiện không có món nào đang chờ.");
                return;
            }

            String separator = "+-------+---------------------------+-------+-----------------+-----------------+";
            System.out.println("\n" + separator);
            System.out.println("|                    DANH SÁCH MÓN ĐỢI DUYỆT                                    |");
            System.out.println(separator);
            System.out.printf("| %-5s | %-25s | %-5s | %-15s | %-15s |%n", "ID", "Tên món", "SL", "Giá", "Trạng thái");
            System.out.println(separator);

            for (OrderItemView i : items) {
                if (!i.getStatus().equals("SERVED") && !i.getStatus().equals("CANCEL")) {
                    System.out.printf("| %-5d | %-25s | %-5d | %-15.0f | %-15s |%n",
                            i.getId(), i.getName(), i.getQuantity(), i.getPriceAtOrder(), i.getStatus());
                }
            }
            System.out.println(separator);

            int orderItemId = InputMethod.inputInt("Nhập ID món muốn DUYỆT (hoặc 0 để thoát): ");
            if (orderItemId == 0) return;

            orderItemService.approveItem(orderItemId);
            System.out.println("DUYỆT THÀNH CÔNG! Món đã sẵn sàng phục vụ.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}