package Ket_Noi_CSDL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Quan_Ly.QuanLyBangCSDL;

public class GiaoDienNguoiDung implements IGiaoDienNguoiDung {
    private String currentTable;
    private JTable table;
    private JComboBox<String> tableSelectionBox;
    private JLabel tableCountLabel;  
    private QuanLyBangCSDL quanLyBangCSDL;  // Đối tượng quản lý bảng

    public GiaoDienNguoiDung() {
        // Khởi tạo đối tượng quản lý bảng
        quanLyBangCSDL = new QuanLyBangCSDL();
        
        // Khởi tạo hộp chọn bảng với các bảng được định sẵn
        tableSelectionBox = new JComboBox<>(new String[] {
            "SINHVIEN","PHANCONG","MONHOC","LOPHOC","GIAOVIEN","DIEM"
        });
        
        // Đặt bảng mặc định là bảng đầu tiên trong danh sách
        currentTable = tableSelectionBox.getItemAt(0); 
        
        // Thêm sự kiện khi người dùng chọn bảng
        tableSelectionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cập nhật currentTable khi người dùng thay đổi lựa chọn
                currentTable = (String) tableSelectionBox.getSelectedItem();
            }
        });
    }

    @Override
    public void createGUI() {
        JFrame frame = new JFrame("Database Query Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        // Tạo bảng và cuộn
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bảng chọn các bảng và các nút truy vấn
        JPanel buttonPanel = new JPanel(new GridLayout(2, 8, 5, 5));

        // Duyệt qua tất cả các bảng và tạo nút truy vấn cho mỗi bảng
        for (int i = 0; i < tableSelectionBox.getItemCount(); i++) {
            String tableName = tableSelectionBox.getItemAt(i);
            JButton btn = new JButton("Truy vấn " + tableName);
            btn.addActionListener(e -> {
                // Khi người dùng nhấn nút truy vấn, cập nhật bảng hiện tại và thực hiện truy vấn
                currentTable = tableName;
                CauLenhSQL.executeQuery("SELECT * FROM " + currentTable, table);
                
                // Tăng số lượng bảng sau mỗi lần truy vấn
                quanLyBangCSDL.incrementTableCount();
                tableCountLabel.setText("Số lượng bảng truy vấn: " + quanLyBangCSDL.getTableCount()); 
            });
            buttonPanel.add(btn);
        }

        frame.add(buttonPanel, BorderLayout.NORTH);

        // Bảng các nút chức năng thêm, sửa, xóa , tìm kiếm
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Nút thêm bản ghi
        JButton btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(e -> {
            if (currentTable != null) {
                CauLenhSQL.addRecord(currentTable, table);  // Thực hiện thêm bản ghi vào bảng hiện tại
            } else {
                JOptionPane.showMessageDialog(frame, "Vui lòng chọn bảng trước khi thêm.");
            }
        });
        actionPanel.add(btnAdd);

        // Nút sửa bản ghi
        JButton btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> {
            if (currentTable != null) {
                CauLenhSQL.editRecord(currentTable, table);  // Sử dụng currentTable cho câu lệnh SQL
            } else {
                JOptionPane.showMessageDialog(frame, "Vui lòng chọn bảng trước khi sửa.");
            }
        });
        actionPanel.add(btnEdit);

        // Nút xóa bản ghi
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> {
            if (currentTable != null) {
                CauLenhSQL.deleteRecord(currentTable, table); 
            } else {
                JOptionPane.showMessageDialog(frame, "Vui lòng chọn bảng trước khi xóa.");
            }
        });
        actionPanel.add(btnDelete);

        // Thêm một label để hiển thị số lượng bảng đã truy vấn
        tableCountLabel = new JLabel("Số lượng bảng truy vấn: 0");
        actionPanel.add(tableCountLabel);

        frame.add(actionPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        
      
        
     // Nút tìm kiếm bản ghi
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> {
            if (currentTable != null) {
                // Hiển thị hộp thoại nhập mã sinh viên
                String maSV = JOptionPane.showInputDialog(frame, "Nhập mã sinh viên để tìm:", "Tìm kiếm sinh viên", JOptionPane.QUESTION_MESSAGE);

                // Kiểm tra mã sinh viên hợp lệ
                if (maSV != null && !maSV.trim().isEmpty()) {
                    // Thực hiện câu lệnh SQL để tìm kiếm sinh viên
                    String query = "SELECT * FROM SINHVIEN WHERE MaSV = '" + maSV.trim() + "'";
                    boolean result = CauLenhSQL.executeQuery(query, table); // 'table' là JTable hiển thị kết quả

                    // Kiểm tra kết quả tìm kiếm
                    if (result) {
                        JOptionPane.showMessageDialog(frame, "Tìm thấy sinh viên với mã: " + maSV, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Không tìm thấy sinh viên với mã: " + maSV, "Kết quả tìm kiếm", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Vui lòng nhập mã sinh viên hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Vui lòng chọn bảng trước khi tìm kiếm.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });
        actionPanel.add(btnSearch);


    }

    @Override
    public void addRecord(JTable table) {
        // Đảm bảo thực hiện các thao tác thêm bản ghi khi có yêu cầu
    }

    @Override
    public void editRecord(JTable table) {
        // Đảm bảo thực hiện các thao tác sửa bản ghi khi có yêu cầu
    }

    @Override
    public void deleteRecord(JTable table) {
        // Đảm bảo thực hiện các thao tác xóa bản ghi khi có yêu cầu
    }

    @Override
    public void searchRecord(JTable table) {
        // Tìm kiếm bản ghi khi cần thiết
    }
}
