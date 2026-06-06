package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Order;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Lọc danh sách đơn hàng theo trạng thái (PENDING, DELIVERED, CANCELLED)
    List<Order> findByOrderStatus(String orderStatus);
    Optional<Order> findByTxnRef(String txnRef);
}