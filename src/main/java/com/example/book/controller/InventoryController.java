package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.service.InventoryService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin("*")
@Tag(name = "API Inventory", description = "All endpionts to mannupulate Inventory: CRUD")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Lấy trạng thái số lượng tồn kho của toàn bộ sách hiện có
    @GetMapping
    public ResponseEntity<?> getReport() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // Lấy danh sách sách sắp hết hàng (React sẽ gọi API này để hiện popup cảnh báo đỏ)
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockProducts());
    }
}