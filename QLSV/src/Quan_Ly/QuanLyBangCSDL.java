package Quan_Ly;

import Ket_Noi_CSDL.CauLenhSQL;
import java.sql.*;

public class QuanLyBangCSDL extends DatabaseManager {  // Kế thừa (Inheritance)
    private static int tableCount = 0;  // Biến tĩnh lưu số lượng bảng (Static Variable)

    private CauLenhSQL cauLenhSQL;  // Kết hợp (Composition) với lớp CauLenhSQL

    public QuanLyBangCSDL() {
        super();  // Gọi constructor của lớp cha (DatabaseManager) (Inheritance)
        cauLenhSQL = new CauLenhSQL(); // Khởi tạo đối tượng CauLenhSQL (Composition)
    }

    public int listTables() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[] {"TABLE"});
            
            // Đếm số lượng bảng và tăng biến tĩnh tableCount (Static Variable)
            while (rs.next()) {
                tableCount++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableCount;
    }

    public void incrementTableCount() {
        tableCount++;  // Tăng số lượng bảng lên 1 (Static Variable)
    }

    public int getTableCount() {
        return tableCount;  // Trả về giá trị của biến tĩnh tableCount (Static Variable)
    }

    public void executeQuery(String sqlQuery) {
        cauLenhSQL.executeQuery(sqlQuery, null);  // Sử dụng CauLenhSQL để thực thi truy vấn (Composition)
    }
    
    
}
