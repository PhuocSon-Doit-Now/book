package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.book.entity.OrderDetail;
import com.example.book.repository.OrderDetailRepository;
import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository odRepo;

    /**
     * LẤY CHI TIẾT CÁC MÓN HÀNG THEO MÃ ĐƠN HÀNG
     * Ứng dụng: Khi Admin bấm nút "Xem chi tiết đơn hàng" trên giao diện React, backend sẽ gọi 
     * hàm này để trả về danh sách các cuốn sách, số lượng mua, và giá tiền tại thời điểm mua của đơn đó.
     */
    public List<OrderDetail> getDetailsByOrderId(Long orderId) {
        return odRepo.findByOrderId(orderId);
    }
}