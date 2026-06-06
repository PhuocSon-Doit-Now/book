package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.OrderDetail;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    // Lấy ra danh sách các cuốn sách nằm trong một đơn hàng cụ thể
    List<OrderDetail> findByOrderId(Long orderId);
}