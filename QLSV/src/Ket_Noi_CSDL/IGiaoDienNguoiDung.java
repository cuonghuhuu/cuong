package Ket_Noi_CSDL;
import javax.swing.JTable;
public interface IGiaoDienNguoiDung {
    void createGUI();                // Phương thức để tạo giao diện chính
    void addRecord(JTable table);    // Phương thức thêm bản ghi
    void editRecord(JTable table);   // Phương thức sửa bản ghi
    void deleteRecord(JTable table); // Phương thức xóa bản ghi
    void searchRecord(JTable table); // Phương thức tìm kiếm bản ghi
}
