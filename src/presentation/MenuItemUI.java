package presentation;

import model.MenuItem;
import model.enums.FoodType;
import service.IMenuItemService;
import service.iml.MenuItemService;
import utils.InputMethod;

import java.util.List;

public class MenuItemUI {
    private IMenuItemService service = new MenuItemService();

    public void menu(){
        while (true){
            try {
                System.out.println("\n===== Menu Item Manager =====");
                System.out.println("1. Thêm menu item");
                System.out.println("2. Danh sách menu item");
                System.out.println("3. Cập nhật menu item");
                System.out.println("4. Xóa menu item");
                System.out.println("5. Tìm kiếm menu item theo loại");
                System.out.println("0. Thoát");

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
                        return;
                    }
                }
            }catch (Exception e){}
        }
    }

    private void createMenuItem() throws Exception{
        String name = InputMethod.inputString("Nhập tên menu item: ");

        System.out.println("Chọn loại đồ ăn:");
        System.out.println("1. Food");
        System.out.println("2. Drink");
        int choice = InputMethod.inputInt("Chọn: ");
        FoodType foodType = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

        double price = InputMethod.inputDouble("Nhập giá menu item: ");
        int stock = InputMethod.inputInt("Nhập số lượng menu item: ");

        service.addMenu(name, foodType, price, stock);
    }

    private void showMenuItem() throws Exception{
        List<MenuItem> list = service.findAll();

        System.out.println("\n===== Danh sách món ăn =====");
        System.out.printf("%-5s %-25s %-10s %-10s %-10s %-10s\n", "ID", "Name", "Price", "Type", "Stock", "Status");
        for (MenuItem item : list) {
            System.out.printf("%-5s %-25s %-10s %-10s %-10s %-10s\n",
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getFoodType(),
                    item.getStock(),
                    item.getStatus()
                    );
        }
    }

    private void updateMenuItem() throws Exception{
        int id = InputMethod.inputInt("Nhập ID cần cập nhật: ");
        MenuItem item = service.findById(id);
        if (item == null) throw new Exception("Không tìm thấy menu item");

        String name = InputMethod.inputString("Nhập tên menu item mới (" + item.getName() + "): ");
        System.out.println("Chọn loại đồ ăn mới: ");
        System.out.println("1. FOOD");
        System.out.println("2. DRINK");
        int choice = InputMethod.inputInt("Chọn: ");
        FoodType foodType = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

        double price = InputMethod.inputDouble("Nhập giá món ăn (" + item.getPrice() + "): ");
        int stock = InputMethod.inputInt("Nhập số lượng món (" + item.getStock() + "): ");

        item.setName(name);
        item.setFoodType(foodType);
        item.setPrice(price);
        item.updateStock(stock);

        service.updateMenuItem(item);
        System.out.println("Cập nhật thành công");
    }

    private void delete() throws Exception{
        int id = InputMethod.inputInt("Nhập ID cần xóa: ");
        service.delete(id);
        System.out.println("Xóa thành công");
    }

    private void searchByType() {
        try {
            System.out.println("Nhập loại đồ ăn:");
            System.out.println("1. FOOD");
            System.out.println("2. DRINK");

            int choice = InputMethod.inputInt("Chọn: ");
            FoodType type = (choice == 1) ? FoodType.FOOD : FoodType.DRINK;

            List<MenuItem> list = service.findByType(type);

            if (list.isEmpty()){
                System.out.println("Không có Menu Item tương ứng");
                return;
            }

            for (MenuItem item : list) {
                System.out.printf("%-5s %-25s %-10s %-10s %-10s %-10s\n",
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getFoodType(),
                        item.getStock(),
                        item.getStatus()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm menu item " +e.getMessage());
        }
    }
}
