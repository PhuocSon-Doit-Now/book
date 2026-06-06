package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.book.entity.Product;
import com.example.book.entity.Inventory;
import com.example.book.repository.ProductRepository;
import com.example.book.repository.InventoryRepository;
import java.util.List;

@Service
public class ProductService {

    // Tiêm các Repository vào để tương tác với Database
    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

    /**
     * LẤY TẤT CẢ SÁCH
     * Giải thích: Gọi hàm findAll() có sẵn của JPA để lấy toàn bộ danh sách sách.
     * Ứng dụng: Dùng để hiển thị danh sách sản phẩm ở trang chủ (phía Khách) 
     * hoặc danh sách quản lý (phía Admin).
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * LẤY CHI TIẾT 1 CUỐN SÁCH THEO ID
     * Giải thích: findById() trả về một đối tượng Optional (có thể có hoặc không). 
     * Dùng .orElseThrow() để nếu không tìm thấy ID đó trong DB thì lập tức báo lỗi.
     * Ứng dụng: Dùng khi khách bấm xem chi tiết một cuốn sách, hoặc khi Admin bấm nút "Sửa".
     */
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuốn sách có ID: " + id));
    }

    /**
     * THÊM MỚI SÁCH (CÓ KHỞI TẠO KHO)
     * Giải thích: `@Transactional` đảm bảo cả 2 hành động (lưu sách và tạo kho) phải cùng thành công.
     * Khi lưu xong `productRepo.save(product)`, ta thu được đối tượng sách có kèm ID tự tăng.
     * Ta lấy ID đó gán sang cho bảng Inventory với số lượng (quantity) mặc định bằng 0.
     * Tại sao phải làm vậy? Vì hệ thống của bạn có Quản lý kho, mỗi cuốn sách bắt buộc phải có một 
     * ô chứa số lượng trong bảng kho, không thể để một cuốn sách tồn tại mà không biết nó có bao nhiêu quyển.
     */
    @Transactional
    public Product saveProduct(Product product) {
        // 1. Lưu thông tin sách cơ bản xuống DB trước
        Product savedProduct = productRepo.save(product);
        
        // 2. Tự động tạo luôn một dòng quản lý tồn kho cho cuốn sách này
        Inventory inventory = Inventory.builder()
                .product(savedProduct) // Gắn mối quan hệ sang cuốn sách vừa tạo
                .quantity(0)          // Sách mới tạo chưa nhập hàng nên số lượng bằng 0
                .minimumStock(5)       // Mặc định dưới 5 quyển là hệ thống báo động sắp hết hàng
                .build();
        inventoryRepo.save(inventory);
        
        return savedProduct;
    }

    /**
     * CẬP NHẬT THÔNG TIN SÁCH (SỬA SÁCH)
     * Giải thích: Tìm cuốn sách cũ trong DB ra. Gán các giá trị mới từ Frontend (productDetails) đè lên.
     * Tại sao không sửa quantity ở đây? Như đã giải thích, số lượng kho chỉ biến động khi có 
     * Đơn Nhập hoặc Đơn Xuất, tuyệt đối không sửa lụi ở form Chỉnh sửa sản phẩm.
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        // 1. Kiểm tra xem sách có tồn tại để sửa không
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách cần cập nhật!"));

        // 2. Cập nhật các trường thông tin hiển thị
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        
        // Nếu Frontend có truyền link ảnh mới (khách có đổi ảnh) thì mới cập nhật
        if (productDetails.getImageUrl() != null) {
            existingProduct.setImageUrl(productDetails.getImageUrl());
        }

        // 3. Lưu lại cuốn sách đã sửa
        return productRepo.save(existingProduct);
    }

    /**
     * XÓA SÁCH
     * Giải thích: Gọi lệnh xóa theo ID. Nhờ cấu hình `ON DELETE CASCADE` ở database 
     * và Entity, dòng tồn kho tương ứng trong bảng `inventories` cũng tự động bay màu theo.
     */
    public void deleteProduct(Long id) {
        // Kiểm tra xem sách có tồn tại không trước khi xóa
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sách để xóa!");
        }
        productRepo.deleteById(id);
    }
}