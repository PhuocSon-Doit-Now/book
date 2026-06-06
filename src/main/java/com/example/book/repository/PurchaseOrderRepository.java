package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}