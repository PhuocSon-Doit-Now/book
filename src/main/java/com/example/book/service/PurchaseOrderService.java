package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.book.entity.PurchaseOrder;
import com.example.book.entity.PurchaseOrderDetail;
import com.example.book.entity.Inventory;
import com.example.book.repository.PurchaseOrderRepository;
import com.example.book.repository.PurchaseOrderDetailRepository;
import com.example.book.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository poRepo;

    @Autowired
    private PurchaseOrderDetailRepository podRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

    /**
     * TẠO ĐƠN NHẬP HÀNG VÀ CỘNG SỐ LƯỢNG VÀO KHO
     * Giải thích: Hàm này nhận vào một Object đơn nhập (chứa thông tin nhà cung cấp) 
     * và một mảng Danh sách các cuốn sách được nhập (details).
     */
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order, List<PurchaseOrderDetail> details) {
        
        // VÒNG LẶP 1: Tính tổng số tiền của toàn bộ hóa đơn nhập hàng này
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderDetail detail : details) {
            // Tiền của 1 loại sách = Giá nhập x Số lượng nhập
            BigDecimal itemTotal = detail.getImportPrice().multiply(new BigDecimal(detail.getQuantity()));
            totalAmount = totalAmount.add(itemTotal); // Cộng dồn vào tổng hóa đơn
        }
        order.setTotalAmount(totalAmount); // Gán tổng tiền vào đơn nhập

        // Bước 1: Lưu đơn nhập hàng tổng quan (Lấy ra được ID đơn nhập)
        PurchaseOrder savedOrder = poRepo.save(order);

        // VÒNG LẶP 2: Lưu chi tiết từng cuốn sách nhập và tiến hành CỘNG KHO
        for (PurchaseOrderDetail detail : details) {
            // Gắn liên kết chi tiết này thuộc về hóa đơn nhập vừa lưu ở Bước 1
            detail.setPurchaseOrder(savedOrder);
            podRepo.save(detail); // Lưu vào bảng purchase_order_details

            // TÌM Ô CHỨA TRONG KHO để tiến hành cộng số lượng
            Inventory inventory = inventoryRepo.findByProductId(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi kho cho sách có ID: " + detail.getProduct().getId()));
            
            // Lấy số lượng cũ trong kho + số lượng vừa nhập mới
            int newQuantity = inventory.getQuantity() + detail.getQuantity();
            inventory.setQuantity(newQuantity); // Cập nhật số lượng mới
            
            inventoryRepo.save(inventory); // Lưu lại bảng inventories (Hoàn tất cộng kho)
        }

        return savedOrder;
    }
}