package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.repository.PurchaseOrderRepository; // Tiêm thẳng repo vì hàm này chỉ đọc dữ liệu cơ bản

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/purchase-orders")
@CrossOrigin("*")
@Tag(name = "API Purchase Order", description = "All endpionts to mannupulate Purchase Order: CRUD")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepo;

    // Xem danh sách tất cả các đơn đã từng nhập kho (Dành cho trang Quản lý lịch sử nhập kho của Admin)
    @GetMapping
    public ResponseEntity<?> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderRepo.findAll());
    }
}