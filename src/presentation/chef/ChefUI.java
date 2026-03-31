package presentation.chef;

import dto.ChefItemView;
import service.IOrderItemService;
import service.impl.OrderItemService;
import utils.InputMethod;

import java.util.List;

public class ChefUI {
    private final IOrderItemService orderItemService = new OrderItemService();

    public boolean menu() {
        while (true) {
            String separator = "+-----------------------------------+";

            System.out.println("\n" + separator);
            System.out.println("|             CHEF MENU             |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. Xem danh sách món cần chuẩn bị");
            System.out.printf("| %-33s |\n", "2. Cập nhật trạng thái món");
            System.out.printf("| %-33s |\n", "0. Đăng xuất / Quay lại");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn chức năng: ");

            switch (choice) {
                case 1 -> showPendingItems();
                case 2 -> updateItemStatus();
                case 0 -> {
                    boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                    if (confirm){
                        return true;
                    }
                    return true;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showPendingItems() {
        try {
            // Lưu ý: Bạn cần khai báo hàm getChefPendingItems() trong Interface và gọi DAO ở Service
            List<ChefItemView> items = orderItemService.getChefPendingItems();

            if (items.isEmpty()) {
                System.out.println("Hiện tại không có món nào cần chuẩn bị. Đầu bếp có thể nghỉ ngơi!");
                return;
            }

            String separator = "+-------+-----------------+----------------------+------------+-------+----------------------+";

            System.out.println("\n" + separator);
            System.out.println("|                                  DANH SÁCH MÓN ĐANG ORDER                                  |");
            System.out.println(separator);

            System.out.printf("| %-5s | %-15s | %-20s | %-10s | %-5s | %-20s |%n",
                    "ID", "Bàn", "Tên món", "Loại", "SL", "Trạng thái hiện tại");
            System.out.println(separator);

            for (ChefItemView i : items) {
                System.out.printf("| %-5d | %-15s | %-20s | %-10s | %-5d | %-20s |%n",
                        i.getOrderItemId(),
                        i.getTableName(),
                        i.getItemName(),
                        i.getType(),
                        i.getQuantity(),
                        i.getStatus());
            }
            System.out.println(separator);
        } catch (Exception e) {
            System.out.println("Lỗi tải danh sách: " + e.getMessage());
        }
    }

    private void updateItemStatus() {
        // 1. Hiển thị lại danh sách
        showPendingItems();

        // 2. Yêu cầu nhập ID
        System.out.println("\n(Nhập 0 để quay lại menu chính)");
        int orderItemId = InputMethod.inputInt("Nhập ID món muốn cập nhật trạng thái: ");

        if (orderItemId == 0) return;

        // 3. Tiến hành cập nhật
        try {
            orderItemService.advanceItemStatus(orderItemId);
            System.out.println("Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}