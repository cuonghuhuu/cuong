package Ket_Noi_CSDL;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class CauLenhSQL {

    // Kết nối cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        return KetNoiCSDL.getConnection(); 
    }

    // Kết nối và xử lý lỗi nếu có
    public static Connection connectDatabase() {
        try {
            return getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return null;
        }
    }

    // Đóng kết nối cơ sở dữ liệu
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thực thi truy vấn SQL và hiển thị kết quả vào JTable
    public static boolean executeQuery(String query, JTable table) {
        try (Connection con = getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            DefaultTableModel model = new DefaultTableModel();
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(rs.getMetaData().getColumnName(i));
            }
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }
            table.setModel(model);
            return true;  // Trả về true nếu có dữ liệu
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }

    // Hiển thị dữ liệu bảng
    public static void displayTableData(JTable table, String tableName) {
        String query = "SELECT * FROM " + tableName;
        executeQuery(query, table);
    }

    // Thêm bản ghi mới vào cơ sở dữ liệu
    public static void addRecord(String currentTable, JTable table) {
        String query = "SELECT TOP 1 * FROM " + currentTable;
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            JPanel panel = new JPanel(new GridLayout(columnCount + 1, 2, 10, 10));
            JTextField[] textFields = new JTextField[columnCount];

            // Tạo các trường nhập liệu
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                panel.add(new JLabel("Nhập " + columnName + ":"));
                textFields[i - 1] = new JTextField(20);
                panel.add(textFields[i - 1]);
            }

            // Hiển thị hộp thoại nhập liệu
            int option = JOptionPane.showConfirmDialog(null, panel, "Nhập dữ liệu cho các cột", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + currentTable + " (");
                StringBuilder valuesBuilder = new StringBuilder("VALUES (");

                for (int i = 0; i < columnCount; i++) {
                    String value = textFields[i].getText();
                    if (value.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Trường " + metaData.getColumnName(i + 1) + " không thể để trống.");
                        return;
                    } 
                    queryBuilder.append(metaData.getColumnName(i + 1)).append(", ");
                    valuesBuilder.append("?, ");
                }

                // Xóa dấu phẩy thừa và hoàn thiện câu lệnh SQL
                queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()).append(") ");
                valuesBuilder.delete(valuesBuilder.length() - 2, valuesBuilder.length()).append(")");

                try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString() + valuesBuilder.toString())) {
                    for (int i = 0; i < columnCount; i++) {
                        pstmt.setString(i + 1, textFields[i].getText());
                    }
                    pstmt.executeUpdate();
                    executeQuery("SELECT * FROM " + currentTable, table);
                    JOptionPane.showMessageDialog(null, "Thêm bản ghi thành công.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Chỉnh sửa bản ghi đã chọn
    public static void editRecord(String currentTable, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để chỉnh sửa.");
            return;
        }

        String query = "SELECT * FROM " + currentTable;
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            JTextField[] textFields = new JTextField[columnCount];

            // Lấy dữ liệu từ dòng đã chọn và điền vào các trường nhập liệu
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object currentValue = table.getValueAt(selectedRow, i - 1);

                JLabel label = new JLabel(columnName);
                JTextField textField;

                // Kiểm tra kiểu dữ liệu của cột và chuyển đổi giá trị về dạng String nếu cần
                if (currentValue instanceof BigDecimal) {
                    textField = new JTextField(currentValue.toString());
                } else {
                    textField = new JTextField(currentValue != null ? currentValue.toString() : "");
                }

                textFields[i - 1] = textField;

                editPanel.add(label);
                editPanel.add(textField);
            }

            JScrollPane scrollPane = new JScrollPane(editPanel);
            int option = JOptionPane.showConfirmDialog(null, scrollPane, "Chỉnh sửa bản ghi", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                StringBuilder updateQuery = new StringBuilder("UPDATE " + currentTable + " SET ");
                for (int i = 0; i < columnCount; i++) {
                    updateQuery.append(metaData.getColumnName(i + 1)).append(" = ?, ");
                }
                updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
                updateQuery.append(" WHERE " + metaData.getColumnName(1) + " = ?");

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery.toString())) {
                    for (int i = 0; i < columnCount; i++) {
                        String value = textFields[i].getText();
                        // Đảm bảo giá trị được truyền vào PreparedStatement dưới dạng đúng kiểu
                        if (value.isEmpty()) {
                            updateStmt.setNull(i + 1, Types.NULL); // Nếu giá trị trống, truyền NULL
                        } else {
                            updateStmt.setString(i + 1, value); // Nếu có giá trị, truyền vào như một String
                        }
                    }

                    String idToUpdate = (String) table.getValueAt(selectedRow, 0);
                    updateStmt.setString(columnCount + 1, idToUpdate);

                    updateStmt.executeUpdate();
                    executeQuery("SELECT * FROM " + currentTable, table);
                    JOptionPane.showMessageDialog(null, "Cập nhật bản ghi thành công.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Xóa bản ghi đã chọn
    public static void deleteRecord(String currentTable, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa.");
            return;
        }

        // Lấy khóa chính từ cột đầu tiên của dòng đã chọn
        String primaryKeyValue = table.getValueAt(selectedRow, 0).toString();
        String primaryKeyColumn = table.getColumnName(0); // Cột đầu tiên là khóa chính

        // Xác nhận trước khi xóa
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa bản ghi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Thực thi truy vấn xóa
        String deleteQuery = "DELETE FROM " + currentTable + " WHERE " + primaryKeyColumn + " = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, primaryKeyValue);
            pstmt.executeUpdate();
            executeQuery("SELECT * FROM " + currentTable, table);
            JOptionPane.showMessageDialog(null, "Xóa bản ghi thành công.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

 // Tìm kiếm bản ghi theo mã sinh viên
    public static void searchRecordByMaSV(JTable table, String currentTable) {
        String searchText = JOptionPane.showInputDialog("Nhập mã sinh viên để tìm kiếm:");
        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        String query = "SELECT * FROM " + currentTable + " WHERE MaSV = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, searchText); // Gắn giá trị cho tham số Mã sinh viên
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            
            // Lấy metadata của bảng để thiết lập tiêu đề các cột
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(rs.getMetaData().getColumnName(i));
            }
            
            // Đọc dữ liệu từ ResultSet và thêm vào model
            boolean hasResult = false;
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
                hasResult = true;
            }

            if (!hasResult) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy sinh viên với mã: " + searchText);
            } else {
                JOptionPane.showMessageDialog(null, "Tìm kiếm thành công!");
            }

            // Gán dữ liệu cho JTable
            table.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra trong quá trình tìm kiếm!");
        }
        
    }

}
