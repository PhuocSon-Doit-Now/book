package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.book.entity.Inventory;
import com.example.book.repository.InventoryRepository;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepo;

    /**
     * XEM BÁO CÁO TOÀN BỘ KHO
     * Giải thích: Lấy ra danh sách gồm: Tên sách, Số lượng hiện tại, Ngưỡng báo động.
     * Ứng dụng: Hiển thị bảng danh sách kho trên giao diện Admin quản lý kho.
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepo.findAll();
    }

    /**
     * LẤY DANH SÁCH SÁCH SẮP HẾT HÀNG
     * Giải thích: Hàm này gọi tới câu lệnh `@Query` tùy biến mà chúng ta viết ở tầng Repository.
     * Nó sẽ lọc ra những dòng nào có `quantity <= minimum_stock`.
     * Ứng dụng: Làm tính năng "Cảnh báo hết hàng" trên Dashboard Admin (Hiện danh sách chữ màu đỏ 
     * để nhắc Admin lo đi nhập thêm hàng).
     */
    public List<Inventory> getLowStockProducts() {
        return inventoryRepo.findLowStockProducts();
    }
}