package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.service.PurchaseOrderDetailService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/purchase-order-details")
@CrossOrigin("*")
@Tag(name = "API Purchase Order Detail", description = "All endpionts to mannupulate Purchase Order Detail: CRUD")
public class PurchaseOrderDetailController {

    @Autowired
    private PurchaseOrderDetailService purchaseOrderDetailService;

    // Lấy danh sách các cuốn sách nằm trong đơn nhập kho cụ thể dựa theo mã đơn nhập
    @GetMapping("/purchase-order/{purchaseOrderId}")
    public ResponseEntity<?> getByPurchaseOrderId(@PathVariable Long purchaseOrderId) {
        return ResponseEntity.ok(purchaseOrderDetailService.getDetailsByOrderId(purchaseOrderId));
    }
}