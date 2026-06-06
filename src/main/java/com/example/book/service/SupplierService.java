package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.book.entity.Supplier;
import com.example.book.repository.SupplierRepository;
import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepo;

    // LẤY TẤT CẢ NHÀ CUNG CẤP
    // Ứng dụng: Admin vào xem danh sách đối tác hoặc chọn Nhà cung cấp khi làm đơn
    // Nhập hàng.
    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    // THÊM HOẶC SỬA NHÀ CUNG CẤP
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    // Bổ sung hàm này vào SupplierService.java
    public Supplier updateSupplier(Integer id, Supplier supplierDetails) {
        // 1. Tìm nhà cung cấp cũ trong DB xem có tồn tại không
        Supplier existingSupplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp cần cập nhật!"));

        // 2. Cập nhật tên và số điện thoại mới
        existingSupplier.setName(supplierDetails.getName());
        existingSupplier.setPhone(supplierDetails.getPhone());

        // 3. Lưu lại xuống database
        return supplierRepo.save(existingSupplier);
    }

    // XÓA NHÀ CUNG CẤP
    // Giải thích: Dùng cơ chế RESTRICT (ngăn chặn xóa). Nếu nhà cung cấp này từng
    // có hóa đơn nhập hàng
    // trong quá khứ, hệ thống sẽ báo lỗi và KHÔNG cho xóa để bảo toàn dữ liệu lịch
    // sử kế toán.
    public void deleteSupplier(Integer id) {
        if (!supplierRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhà cung cấp để xóa!");
        }
        supplierRepo.deleteById(id);
    }
}