package presentation.manager;

import model.MenuItem;
import model.enums.FoodType;
import service.IMenuItemService;
import service.impl.MenuItemService;
import utils.InputMethod;

import java.text.DecimalFormat;
import java.util.List;

public class MenuItemUI {
    private IMenuItemService service = new MenuItemService();

    public void menu(){
        while (true){
            try {
                String separator = "+-----------------------------------+";

                System.out.println("\n" + separator);
                System.out.println("|         MENU ITEM MANAGER         |");
                System.out.println(separator);
                System.out.printf("| %-33s |\n", "1. Thêm menu item");
                System.out.printf("| %-33s |\n", "2. Danh sách menu item");
                System.out.printf("| %-33s |\n", "3. Cập nhật menu item");
                System.out.printf("| %-33s |\n", "4. Xóa menu item");
                System.out.printf("| %-33s |\n", "5. Tìm kiếm menu item theo loại");
                System.out.printf("| %-33s |\n", "0. Thoát");
                System.out.println(separator);

                int choice = InputMethod.inputInt("Chọn: ");

                switch (choice){
                    case 1 -> {
                        createMenuItem();
                    }
                    case 2 -> {
                        showMenuItem();
                    }
                    case 3 -> {
                        updateMenuItem();
                    }
                    case 4 -> {
                        delete();
                    }
                    case 5 -> {
                        searchByType();
                    }
                    case 0 -> {
                        boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn đăng xuất");
                        if (confirm){
                            return;
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createMenuItem() throws Exception{
        String name = InputMethod.inputString("Nhập tên món ăn: ");

        System.out.println("Chọn loại đồ ăn:");
        System.out.println("1. Food");
        System.out.println("2. Drink");
        int choice = InputMethod.inputInt("Chọn: ");
        FoodType foodType = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

        double price = InputMethod.inputDouble("Nhập giá món ăn: ");
        int stock = InputMethod.inputInt("Nhập số lượng món ăn: ");

        service.addMenu(name, foodType, price, stock);
        System.out.println("Thêm thành công");
    }

    private void showMenuItem() throws Exception{
        List<MenuItem> list = service.findAll();

        String separator = "+-------+---------------------------+-----------------+------------+------------+-----------------+";

        System.out.println("\n" + separator);

        System.out.println("|                                       DANH SÁCH MÓN ĂN                                          |");
        System.out.println(separator);

        System.out.printf("| %-5s | %-25s | %-15s | %-10s | %-10s | %-15s |%n",
                "ID", "Name", "Price", "Type", "Stock", "Status");
        System.out.println(separator);

        DecimalFormat df = new DecimalFormat("#,### VNĐ");

        for (MenuItem item : list) {
            System.out.printf("| %-5s | %-25s | %-15s | %-10s | %-10s | %-15s |%n",
                    item.getId(),
                    item.getName(),
                    df.format(item.getPrice()),
                    item.getFoodType(),
                    item.getStock(),
                    item.getStatus()
            );
        }
        System.out.println(separator);
    }

    private void updateMenuItem() throws Exception{
        try {
            int id = InputMethod.inputInt("Nhập ID cần cập nhật: ");
            MenuItem item = service.findById(id);
            if (item == null) System.out.println("Không tìm thấy món ăn");

            String name = InputMethod.inputString("Nhập tên món ăn mới (" + item.getName() + "): ");

            String separator = "+-----------------------------------+";

            System.out.println("\n" + separator);
            System.out.println("|         CHỌN LOẠI ĐỒ ĂN           |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. FOOD");
            System.out.printf("| %-33s |\n", "2. DRINK");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn (1 or 2): ");
            FoodType foodType = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

            double price = InputMethod.inputDouble("Nhập giá món ăn (" + item.getPrice() + "): ");
            int stock = InputMethod.inputInt("Nhập số lượng món (" + item.getStock() + "): ");

            item.setName(name);
            item.setFoodType(foodType);
            item.setPrice(price);
            item.updateStock(stock);

            service.updateMenuItem(item);
            System.out.println("Cập nhật thành công");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void delete() throws Exception{
        try {
            int id = InputMethod.inputInt("Nhập ID cần xóa: ");
            service.delete(id);
            System.out.println("Xóa món ăn thành công!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchByType() {
        String separator = "+-----------------------------------+";

        try {
            System.out.println("\n" + separator);
            System.out.println("|         CHỌN LOẠI ĐỒ ĂN           |");
            System.out.println(separator);
            System.out.printf("| %-33s |\n", "1. FOOD");
            System.out.printf("| %-33s |\n", "2. DRINK");
            System.out.println(separator);

            int choice = InputMethod.inputInt("Chọn (1 or 2): ");
            FoodType type = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

            List<MenuItem> list = service.findByType(type);

            if (list == null || list.isEmpty()){
                System.out.println("Không có món ăn tương ứng!");
                return;
            }

            // Đã bổ sung Tiêu đề bảng cho xịn xò và đồng bộ
            String tableSeparator = "+-------+---------------------------+-----------------+------------+------------+-----------------+";
            System.out.println("\n" + tableSeparator);
            System.out.println("|                                KẾT QUẢ TÌM KIẾM                                                 |");
            System.out.println(tableSeparator);
            System.out.printf("| %-5s | %-25s | %-15s | %-10s | %-10s | %-15s |%n",
                    "ID", "Name", "Price", "Type", "Stock", "Status");
            System.out.println(tableSeparator);

            DecimalFormat df = new DecimalFormat("#,### VNĐ");

            for (MenuItem item : list) {
                System.out.printf("| %-5s | %-25s | %-15s | %-10s | %-10s | %-15s |%n",
                        item.getId(),
                        item.getName(),
                        df.format(item.getPrice()),
                        item.getFoodType(),
                        item.getStock(),
                        item.getStatus()
                );
            }
            System.out.println(tableSeparator);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
