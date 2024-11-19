package MAIN;
import Quan_Ly.QuanLyBangCSDL;
// import Quan_Ly.QuanLyNhapLieu;
// import Quan_Ly.ThucThiThongKe;
// import Quan_Ly.QuanLyNguoiDung;
import Ket_Noi_CSDL.KetNoiCSDL;

import javax.swing.UIManager;

import Ket_Noi_CSDL.CauLenhSQL;
import Ket_Noi_CSDL.IGiaoDienNguoiDung;
import Ket_Noi_CSDL.GiaoDienNguoiDung;

public class Main {
    public static void main(String[] args) {
        
        // Kết nối cơ sở dữ liệu
        KetNoiCSDL.getConnection(); 
        
        // Khởi tạo giao diện người dùng
        GiaoDienNguoiDung giaoDien = new GiaoDienNguoiDung();
        // Tạo và hiển thị giao diện
        giaoDien.createGUI();
        // tạo giao diện đẹp hơnn
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new GiaoDienNguoiDung();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
