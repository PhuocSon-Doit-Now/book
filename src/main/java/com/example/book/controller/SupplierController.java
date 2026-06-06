package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.entity.Supplier;
import com.example.book.service.SupplierService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin("*")
@Tag(name = "API Supplier", description = "All endpionts to mannupulate Supplier: CRUD")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.saveSupplier(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Xóa nhà cung cấp thành công!");
    }
}