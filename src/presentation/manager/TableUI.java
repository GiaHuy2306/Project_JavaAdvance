package presentation.manager;

import model.Table;
import model.enums.TableStatus;
import service.ITableService;
import service.impl.TableService;
import utils.InputMethod;

import java.util.List;

public class TableUI {
    private ITableService service = new TableService();

    public void menu(){
        while (true){
            try {
                System.out.println("\n===== Table Manager =====");
                System.out.println("1. Thêm bàn");
                System.out.println("2. Danh sách bàn");
                System.out.println("3. Cập nhật bàn");
                System.out.println("4. Xóa bàn");
                System.out.println("5. Tìm kiếm bàn theo trạng thái");
                System.out.println("0. Thoát");

                int choice = InputMethod.inputInt("Chọn: ");

                switch (choice){
                    case 1 -> {
                        createTable();
                    }
                    case 2 -> {
                        showTable();
                    }
                    case 3 -> {
                        updateTable();
                    }
                    case 4 -> {
                        deleteTable();
                    }
                    case 5 -> {
                        searchTableByType();
                    }
                    case 0 -> {
                        return;
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private void createTable() throws Exception {
        String name;
        while (true) {
            name = InputMethod.inputString("Nhập tên bàn: ").trim();

            if (name.isEmpty()){
                System.out.println("Tên không được để trống");
                continue;
            }

            if (service.existsByName(name)){
                System.out.println("Tên bàn đã tồn tại");
                continue;
            }
            break;
        }

        int capacity;
        while (true) {
            capacity = InputMethod.inputInt("Nhập số lượng chỗ ngồi: ");

            if (capacity <= 0) {
                System.out.println("Chỗ ngồi phải > 0");
                continue;
            }

            break;
        }

        TableStatus status;
        while (true) {
            System.out.println("1. EMPTY");
            System.out.println("2. FULL");

            int choice = InputMethod.inputInt("Chọn trạng thái: ");

            if (choice == 1) {
                status = TableStatus.EMPTY;
            } else if (choice == 2) {
                status = TableStatus.FULL;
            } else {
                System.out.println("Lựa chọn không hợp lệ");
                continue;
            }

            break;
        }

        while (true) {
            try {
                Table table = new Table(0, name, capacity, status);
                service.addTable(table);

                System.out.println("Thêm thành công");

                break;

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showTable() throws Exception{
        List<Table> list = service.findAll();

        System.out.println("\n===== Danh sách bàn =====");
        System.out.printf("%-5s %-15s %-10s %-10s\n",
                "ID", "Tên bàn", "Sức chứa", "Trạng thái");

        for (Table t : list) {
            System.out.printf("%-5s %-15s %-10s %-10s\n",
                    t.getId(), t.getName(), t.getCapacity(), t.getStatus());
        }
    }

    private void updateTable() throws Exception{
        int id = InputMethod.inputInt("Nhập ID cần cập nhật: ");
        Table table = service.findById(id);
        if (table == null) System.out.println("Không tìm thấy bàn");

        String name;
        while (true) {
            name = InputMethod.inputString("Nhập tên bàn mới (" + table.getName() + "): ").trim();

            if (name.isEmpty()){
                System.out.println("Tên không được để trống");
                continue;
            }

            if (service.existsByName(name)){
                System.out.println("Tên bàn đã tồn tại");
                continue;
            }
            break;
        }

        int capacity;
        while (true) {
            capacity = InputMethod.inputInt("Nhập số lượng chỗ ngồi mới (" + table.getCapacity() + "): ");

            if (capacity <= 0) {
                System.out.println("Chỗ ngồi phải > 0");
                continue;
            }

            break;
        }

        TableStatus status;
        while (true) {
            System.out.println("1. EMPTY");
            System.out.println("2. FULL");

            int choice = InputMethod.inputInt("Chọn trạng thái mới (" +table.getStatus().name() + "): ");

            if (choice == 1) {
                status = TableStatus.EMPTY;
            } else if (choice == 2) {
                status = TableStatus.FULL;
            } else {
                System.out.println("Lựa chọn không hợp lệ");
                continue;
            }

            break;
        }

        while (true) {
            try {
                table.setName(name);
                table.setCapacity(capacity);
                table.setStatus(status);

                service.updateTable(table);
                System.out.println("Cập nhật thành công");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            break;
        }
    }

    private void deleteTable() {
        int id;

        while (true) {
            id = InputMethod.inputInt("Nhập ID cần xóa: ");
            if (id > 0) break;
            System.out.println("ID phải > 0");
        }

        boolean confirm = InputMethod.inputConfirm("Bạn có chắc chắn muốn xóa không? ");
        if (!confirm) {
            System.out.println("Đã hủy xóa");
            return;
        }

        try {
            service.deleteTable(id);
            System.out.println("Xóa thành công");

        } catch (Exception e) {
            System.out.println("Xóa thất bại: " +e.getMessage());
        }
    }

    private void searchTableByType() {
        TableStatus status = null;
        try {
            System.out.println("Nhập trạng thái bàn muốn tìm: ");
            System.out.println("1. EMPTY");
            System.out.println("2. FULL");

            int choice = InputMethod.inputInt("Chọn: ");
            if (choice == 1) {
                status = TableStatus.EMPTY;
            } else if (choice == 2) {
                status = TableStatus.FULL;
            } else {
                System.out.println("Lựa chọn không hợp lệ");
            }

            List<Table> list = service.findStatusTable(status);

            if (list.isEmpty()){
                System.out.println("Không có bàn tương ứng");
                return;
            }

            for (Table t : list) {
                System.out.printf("%-5s %-15s %-10s %-10s\n",
                        t.getId(), t.getName(), t.getCapacity(), t.getStatus());
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm bàn: " +e.getMessage());
        }
    }
}