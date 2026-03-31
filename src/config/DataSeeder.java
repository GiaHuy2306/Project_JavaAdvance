package config;

import utils.DBConnection;
import utils.HashPassword;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DataSeeder {

    public static void seed() {
        System.out.println("Initializing database...");

        runSQLFile("src/resources/restaurant.sql");
        runSQLFile("src/resources/action.sql");

        addManager("manager", "123456");

        System.out.println("Database ready!");
    }

    private static void runSQLFile(String fileName) {
        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader reader = new BufferedReader(new FileReader(fileName))
        ) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // bỏ qua comment
                if (line.startsWith("--") || line.isEmpty()) continue;

                sql.append(line).append(" ");

                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql.setLength(0);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chạy " + fileName + ": " + e.getMessage());
        }
    }

    private static void addManager(String username, String password) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            //Kiểm tra xem username đã tồn tại chưa
            var rs = stmt.executeQuery(
                    "SELECT COUNT(*) AS count FROM Users WHERE username = '" + username + "';"
            );
            rs.next();
            int count = rs.getInt("count");

            if (count > 0) {
                return;
            }

            //Nếu chưa có, insert admin mới
            String hashed = HashPassword.hash(password);
            String sql = String.format(
                    "INSERT INTO Users(username, password, role, status) " +
                            "VALUES('%s', '%s', 'MANAGER', 'ACTIVE');",
                    username, hashed
            );

            stmt.execute(sql);

        } catch (Exception e) {
//            System.out.println("Lỗi khi thêm admin user: " + e.getMessage());
        }
    }
}