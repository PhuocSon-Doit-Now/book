package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.book.entity.Category;
import com.example.book.repository.CategoryRepository;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    // LẤY TẤT CẢ DANH MỤC
    // Ứng dụng: Đổ ra thanh Menu phía React cho khách chọn thể loại, hoặc đổ vào
    // thẻ <select> khi Admin thêm sách.
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    // THÊM HOẶC CẬP NHẬT DANH MỤC
    // Ứng dụng: Màn hình quản lý danh mục của Admin.
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    // Bổ sung hàm này vào CategoryService.java
    public Category updateCategory(Integer id, Category categoryDetails) {
        // 1. Tìm danh mục cũ trong DB xem có tồn tại không
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục sách cần cập nhật!"));

        // 2. Cập nhật tên mới từ Frontend gửi về
        existingCategory.setName(categoryDetails.getName());

        // 3. Lưu lại xuống database
        return categoryRepo.save(existingCategory);
    }

    // XÓA DANH MỤC
    // Giải thích: Nếu xóa danh mục, các cuốn sách thuộc danh mục này sẽ bị gán
    // category_id = NULL
    // (nhờ cấu hình ON DELETE SET NULL ở DB), giúp sách không bị xóa oan theo danh
    // mục.
    public void deleteCategory(Integer id) {
        if (!categoryRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy danh mục để xóa!");
        }
        categoryRepo.deleteById(id);
    }
}