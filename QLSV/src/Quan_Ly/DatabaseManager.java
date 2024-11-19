package Quan_Ly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    protected Connection connection;

    public DatabaseManager() {
        try {
            // Kết nối cơ sở dữ liệu (thay đổi thông tin kết nối theo yêu cầu)
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QLSV;user=sa;password=123;encrypt=false");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức kết nối cơ sở dữ liệu (Có thể mở rộng trong các lớp con)
    public Connection getConnection() {
        return connection;
    }
}
