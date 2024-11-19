package Ket_Noi_CSDL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KetNoiCSDL {
    private static String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=QLSV;user=sa;password=123;encrypt=false";
    static {
        try {  
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KetNoiCSDL.class.getName()).log(Level.SEVERE, "Driver không được tìm thấy", ex);
            System.out.println("Driver không được tìm thấy. Vui lòng kiểm tra lại.");
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("TRUY VẤN THÀNH CÔNG");
        } catch (SQLException ex) {
            Logger.getLogger(KetNoiCSDL.class.getName()).log(Level.SEVERE, "KET_NOI_THAT_BAI_ERROR!", ex);
            System.out.println("TRUY VẤN THẤT BẠI" + ex.getMessage());
        }
        return conn;
    }
}