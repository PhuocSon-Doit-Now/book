package com.example.book.dto;

import com.example.book.entity.Order;
import com.example.book.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private Order order;
    private List<OrderDetail> details;
}