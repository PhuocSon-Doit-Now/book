package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Inventory;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // Tìm kiếm thông tin kho của một sản phẩm cụ thể
    Optional<Inventory> findByProductId(Long productId);

    // Câu lệnh JPQL cực kỳ đắt giá cho đồ án: Tìm danh sách sách sắp hết hàng trong kho
    // Dùng để lấy dữ liệu làm chức năng hiển thị thông báo/cảnh báo trên giao diện Admin (React)
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minimumStock")
    List<Inventory> findLowStockProducts();
}