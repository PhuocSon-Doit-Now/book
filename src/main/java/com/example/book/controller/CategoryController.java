package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.entity.Category;
import com.example.book.service.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
@Tag(name = "API Category", description = "All endpionts to mannupulate Category: CRUD")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Lấy tất cả danh mục (Khách xem menu / Admin xem bảng)
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 2. Thêm mới danh mục
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    // 3. Cập nhật danh mục theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    // 4. Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Xóa danh mục thành công!");
    }
}