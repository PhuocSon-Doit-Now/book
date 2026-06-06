package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.book.entity.Product;
import com.example.book.entity.Category;
import com.example.book.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
@Tag(name = "API Product", description = "All endpionts to mannupulate Product: CRUD")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Đường dẫn vật lý để lưu ảnh trong folder dự án Spring Boot
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    // 1. Lấy tất cả sách (Cho cả trang chủ React và bảng Admin)
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 2. Xem chi tiết 1 cuốn sách
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 3. THÊM SÁCH MỚI KÈM UPLOAD ẢNH (Tên ảnh random 32 ký tự)
    @PostMapping("/upload")
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        try {
            String fileName = "default.jpg";

            if (file != null && !file.isEmpty()) {
                // 1. Lấy tên file gốc từ frontend gửi lên
                String originalFilename = file.getOriginalFilename();

                // 2. Tách lấy phần mở rộng (đuôi file ví dụ: .jpg, .png)
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 3. Sinh chuỗi ngẫu nhiên 32 ký tự sạch (đã xóa các dấu gạch ngang)
                String random32Chars = UUID.randomUUID().toString().replace("-", "");

                // 4. Tổ hợp lại thành tên file mới hoàn toàn độc nhất
                fileName = random32Chars + extension;

                // 5. Kiểm tra và thực hiện lưu file vật lý xuống ổ cứng
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Đóng gói data nhận được thành Object Product để chuyển sang tầng Service
            Product product = Product.builder()
                    .name(name)
                    .price(BigDecimal.valueOf(price))
                    .description(description)
                    .category(Category.builder().id(categoryId).build()) // Ghép nhanh ID danh mục
                    .imageUrl("/images/" + fileName)
                    .build();

            return ResponseEntity.ok(productService.saveProduct(product));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Lỗi upload ảnh: " + e.getMessage());
        }
    }

    // 4. SỬA SÁCH KÈM KIỂM TRA ẢNH MỚI (Tên ảnh random 32 ký tự)
    @PutMapping("/upload/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile file) {
        try {
            Product productDetails = Product.builder()
                    .name(name)
                    .price(BigDecimal.valueOf(price))
                    .description(description)
                    .build();

            // Nếu Admin thực sự chọn một file ảnh mới thay thế thì mới xử lý
            if (file != null && !file.isEmpty()) {
                // 1. Lấy tên file gốc
                String originalFilename = file.getOriginalFilename();

                // 2. Tách lấy đuôi file
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 3. Tạo chuỗi ngẫu nhiên 32 ký tự sạch
                String random32Chars = UUID.randomUUID().toString().replace("-", "");

                // 4. Ghép lại thành tên file mới
                String fileName = random32Chars + extension;

                // 5. Lưu đè file ảnh mới vào thư mục lưu trữ
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                // 6. Gán đường dẫn ảnh mới vào Object chi tiết
                productDetails.setImageUrl("/images/" + fileName);
            }

            return ResponseEntity.ok(productService.updateProduct(id, productDetails));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật ảnh: " + e.getMessage());
        }
    }

    // 5. Xóa sách
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Xóa sách thành công!");
    }
}