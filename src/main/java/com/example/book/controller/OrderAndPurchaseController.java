package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.book.dto.CheckoutRequest;
import com.example.book.entity.Order;
import com.example.book.entity.OrderDetail;
import com.example.book.entity.PurchaseOrder;
import com.example.book.service.OrderService;
import com.example.book.service.OrderDetailService;
import com.example.book.service.PurchaseOrderService;
import com.example.book.service.VNPayService;

// import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import com.example.book.service.PurchaseOrderDetailService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
@Tag(name = "API Order And Purchase", description = "All endpionts to mannupulate Order And Purchase: CRUD")
public class OrderAndPurchaseController {

    @lombok.Data
    class UpdateStatusRequest {
        private String status;
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderDetailService purchaseOrderDetailService;

    @Autowired
    private VNPayService vnPayService;

    // ==========================================
    // LUỒNG 1: KHÁCH ĐẶT HÀNG (XUẤT KHO)
    // ==========================================

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequest request,
            HttpServletRequest httpRequest) {

        Order order = request.getOrder();
        List<OrderDetail> details = request.getDetails();

        String method = order.getPaymentMethod();

        if ("COD".equalsIgnoreCase(method)) {
            return ResponseEntity.ok(
                    orderService.createCustomerOrder(order, details));
        }

        if ("VNPAY".equalsIgnoreCase(method)) {

            Order pending = orderService.createPendingOrder(order, details);

            String url = vnPayService.createPaymentUrl(pending, httpRequest);

            return ResponseEntity.ok(
                    Map.of("paymentUrl", url));
        }

        return ResponseEntity.badRequest()
                .body("INVALID PAYMENT METHOD");
    }

    // Admin xem toàn bộ danh sách đơn hàng khách đã mua
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Admin bấm vào 1 đơn hàng để xem chi tiết bên trong khách mua những cuốn sách
    // nào
    @GetMapping("/orders/{id}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderDetailService.getDetailsByOrderId(id));
    }

    // API CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG - DÙNG THẲNG ENTITY ORDER KHÔNG DÙNG DTO
    // URL: PUT http://localhost:8080/api/transactions/orders/1/status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id, 
            @RequestBody Order order) { // Đổi từ DTO thành Entity Order ở đây
        try {
            // Lấy trường orderStatus trực tiếp từ object order nhận được
            Order updatedOrder = orderService.updateOrderStatus(id, order.getOrderStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống khi cập nhật đơn hàng: " + e.getMessage());
        }
    }
    // ==========================================
    // LUỒNG 2: ADMIN NHẬP HÀNG (CỘNG KHO)
    // ==========================================

    // Admin thực hiện làm form nhập sách từ Nhà cung cấp
    @PostMapping("/purchase-import")
    public ResponseEntity<?> importBooks(@RequestBody PurchaseRequest request) {
        PurchaseOrder savedPo = purchaseOrderService.createPurchaseOrder(request.getPurchaseOrder(),
                request.getDetails());
        return ResponseEntity.ok(savedPo);
    }

    // Xem chi tiết một đơn nhập hàng cũ đã nhập những sách gì
    @GetMapping("/purchase-import/{id}/details")
    public ResponseEntity<?> getPurchaseDetails(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderDetailService.getDetailsByOrderId(id));
    }
}

// =========================================================================
// CÁC LỚP ĐÓNG GÓI DỮ LIỆU (DTO) - Hỗ trợ hứng Cục JSON phức tạp từ React gửi
// lên
// =========================================================================
@lombok.Data
class OrderRequest {
    private Order order;
    private List<com.example.book.entity.OrderDetail> details;
}

@lombok.Data
class PurchaseRequest {
    private PurchaseOrder purchaseOrder;
    private List<com.example.book.entity.PurchaseOrderDetail> details;
}