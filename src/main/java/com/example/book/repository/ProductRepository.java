package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Product;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Kiểu ID của Product là Long
    
    // Viết sẵn hàm tìm sách theo danh mục để sau này làm bộ lọc trên React
    List<Product> findByCategoryId(Integer categoryId);
}