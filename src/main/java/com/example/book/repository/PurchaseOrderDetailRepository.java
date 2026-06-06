package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.PurchaseOrderDetail;
import java.util.List;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
    
    // Tìm các chi tiết nhập hàng thuộc về một đơn nhập cụ thể
    List<PurchaseOrderDetail> findByPurchaseOrderId(Long purchaseOrderId);
}