package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.book.entity.Order;
import com.example.book.entity.OrderDetail;
import com.example.book.entity.Inventory;
import com.example.book.repository.OrderRepository;
import com.example.book.repository.OrderDetailRepository;
import com.example.book.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderDetailRepository odRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

    /**
     * NGHIỆP VỤ ĐẶT HÀNG VÀ TỰ ĐỘNG TRỪ KHO
     * Giải thích: Nhận thông tin Khách hàng (Form điền ở React) và Danh sách sản
     * phẩm từ Giỏ hàng (LocalStorage).
     */
    @Transactional
    public Order createCustomerOrder(Order order, List<OrderDetail> details) {

        // VÒNG LẶP 1: Tính tổng số tiền khách phải trả cho đơn hàng này
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderDetail detail : details) {
            // Tiền 1 món = Giá bán lẻ hiện tại x Số lượng mua
            BigDecimal itemTotal = detail.getPrice().multiply(new BigDecimal(detail.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        order.setTotalPrice(totalPrice);
        order.setOrderStatus("PENDING"); // Đơn hàng mới luôn ở trạng thái "Chờ duyệt"

        // Bước 1: Lưu thông tin đơn hàng tổng quan (Để lấy ID đơn hàng)
        Order savedOrder = orderRepo.save(order);

        // VÒNG LẶP 2: Kiểm tra kho, tiến hành TRỪ KHO và lưu chi tiết đơn hàng
        for (OrderDetail detail : details) {

            // 1. Kiểm tra xem mặt hàng này trong kho còn hàng không
            Inventory inventory = inventoryRepo.findByProductId(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Sách không tồn tại trong hệ thống kho!"));

            // BẪY LOGIC: Nếu số lượng khách mua lớn hơn số lượng thực tế trong kho -> Báo
            // lỗi sập luồng
            if (inventory.getQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Sách '" + detail.getProduct().getName() + "' hiện tại chỉ còn "
                        + inventory.getQuantity() + " cuốn. Không đủ số lượng cung cấp!");
            }

            // 2. Nếu đủ hàng -> Tiến hành TRỪ KHO
            int newQuantity = inventory.getQuantity() - detail.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryRepo.save(inventory); // Lưu cập nhật số lượng tồn kho mới xuống DB

            // 3. Lưu thông tin cuốn sách này vào bảng chi tiết hóa đơn
            detail.setOrder(savedOrder); // Gắn hóa đơn tổng ở bước 1 vào
            odRepo.save(detail); // Lưu bảng order_details
        }

        return savedOrder;
    }

    @Transactional
    public Order createPendingOrder(
            Order order,
            List<OrderDetail> details) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderDetail detail : details) {

            BigDecimal itemTotal = detail.getPrice().multiply(
                    new BigDecimal(detail.getQuantity()));

            totalPrice = totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);

        order.setOrderStatus("PENDING");

        order.setPaymentMethod("VNPAY");

        // ⭐ QUAN TRỌNG
        order.setTxnRef(
                String.valueOf(System.currentTimeMillis()));

        Order savedOrder = orderRepo.save(order);

        for (OrderDetail detail : details) {

            detail.setOrder(savedOrder);

            odRepo.save(detail);
        }

        return savedOrder;
    }

    @Transactional
    public void confirmVNPayOrder(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("PAID".equals(order.getOrderStatus())) {
            return;
        }

        List<OrderDetail> details = odRepo.findByOrderId(orderId);

        // 🔥 CHECK TRƯỚC TOÀN BỘ KHO (quan trọng)
        for (OrderDetail detail : details) {

            Inventory inventory = inventoryRepo.findByProductId(
                    detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (inventory.getQuantity() < detail.getQuantity()) {
                throw new RuntimeException(
                        "Không đủ hàng cho sản phẩm: " +
                                detail.getProduct().getName());
            }
        }

        // 🔥 TRỪ KHO SAU KHI CHECK OK
        for (OrderDetail detail : details) {

            Inventory inventory = inventoryRepo.findByProductId(
                    detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            inventory.setQuantity(
                    inventory.getQuantity() - detail.getQuantity());

            inventoryRepo.save(inventory);
        }

        order.setOrderStatus("PAID");

        orderRepo.save(order);
    }

    /**
     * LẤY TOÀN BỘ ĐƠN HÀNG (Cho Admin)
     * Ứng dụng: Admin vào xem danh sách hóa đơn khách đặt để bấm nút "Duyệt đơn"
     * hoặc "Hủy đơn".
     */
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    

    /**
     * TÌM KIẾM ĐƠN HÀNG THEO MÃ TXNREF (CHÍNH LÀ ID ĐƠN HÀNG)
     * Nhiệm vụ: Nhận chuỗi txnRef từ VNPay, ép về kiểu Long và tìm kiếm trong DB.
     */
    public Order findByTxnRef(String txnRef) {
        if (txnRef == null || txnRef.trim().isEmpty()) {
            throw new RuntimeException("Mã giao dịch txnRef gửi lên bị trống!");
        }

        // Gọi hàm tìm kiếm theo cột txn_ref truyền vào tham số kiểu String chuẩn chỉnh
        return orderRepo.findByTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã giao dịch txnRef: " + txnRef));
    }

    /**
     * CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG (Dành cho trang Quản lý Admin)
     * Nghiệp vụ xử lý: 
     * - Nếu chuyển trạng thái sang "CANCELLED" (Hủy đơn): Hoàn lại số lượng sách vào kho.
     * - Chặn không cho cập nhật nếu đơn hàng đã bị hủy hoặc đã hoàn thành trước đó.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        // 1. Kiểm tra tham số đầu vào đề phòng Frontend truyền thiếu
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new RuntimeException("Trạng thái đơn hàng cập nhật không được để trống!");
        }

        // 2. Tìm đơn hàng trong DB
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        String currentStatus = order.getOrderStatus();

        // 3. Bẫy logic trạng thái cũ
        if ("CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Không thể cập nhật đơn hàng đã bị HỦY!");
        }
        if ("DELIVERED".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Không thể cập nhật đơn hàng đã GIAO THÀNH CÔNG!");
        }

        // Đảm bảo chữ hoa chuẩn chỉnh trước khi so sánh và lưu
        String upperStatus = newStatus.trim().toUpperCase();

        // 4. Nếu hủy đơn thì hoàn sách về lại kho
        if ("CANCELLED".equals(upperStatus)) {
            List<OrderDetail> details = odRepo.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                Inventory inventory = inventoryRepo.findByProductId(detail.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sách không tồn tại trong kho để hoàn trả!"));

                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventoryRepo.save(inventory);
            }
        }

        // 5. Cập nhật và lưu lại đơn hàng
        order.setOrderStatus(upperStatus);
        return orderRepo.save(order);
    }
}