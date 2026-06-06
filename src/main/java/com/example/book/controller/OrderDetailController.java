package com.example.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.book.service.OrderDetailService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/order-details")
@CrossOrigin("*")
@Tag(name = "API Order Detail", description = "All endpionts to mannupulate Order Detail: CRUD")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // Lấy chi tiết các cuốn sách nằm trong 1 đơn hàng cụ thể theo mã đơn đặt
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderDetailService.getDetailsByOrderId(orderId));
    }
}