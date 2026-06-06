package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.book.entity.PurchaseOrderDetail;
import com.example.book.repository.PurchaseOrderDetailRepository;
import java.util.List;

@Service
public class PurchaseOrderDetailService {

    @Autowired
    private PurchaseOrderDetailRepository podRepo;

    /**
     * LẤY CHI TIẾT THEO MÃ ĐƠN NHẬP
     * Giải thích: Khi Admin bấm xem lại một hóa đơn nhập hàng cũ, hàm này sẽ tìm toàn bộ 
     * các dòng sách nằm trong đơn nhập đó dựa vào `purchaseOrderId`.
     */
    public List<PurchaseOrderDetail> getDetailsByOrderId(Long purchaseOrderId) {
        return podRepo.findByPurchaseOrderId(purchaseOrderId);
    }
}