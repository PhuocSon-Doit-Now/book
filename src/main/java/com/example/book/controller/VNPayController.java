package com.example.book.controller;

import com.example.book.config.VNPayConfig;
import com.example.book.entity.Order;
import com.example.book.service.OrderService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/vnpay")
@CrossOrigin("*")
@Tag(name = "VNPay API")
public class VNPayController {

        @Autowired
        private OrderService orderService;

        @Value("${vnpay.vnp_Url}")
        private String vnp_Url;

        @Value("${vnpay.vnp_TmnCode}")
        private String vnp_TmnCode;

        @Value("${vnpay.vnp_HashSecret}")
        private String vnp_HashSecret;

        @Value("${vnpay.vnp_ReturnUrl}")
        private String vnp_ReturnUrl;

        @GetMapping("/create-payment")
        public ResponseEntity<?> createPayment(
                        @RequestParam("amount") long amount,
                        HttpServletRequest request) {

                try {

                        String vnp_Version = "2.1.0";
                        String vnp_Command = "pay";
                        String orderType = "other";

                        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());

                        String vnp_OrderInfo = "Thanh toan don hang";

                        String vnp_IpAddr = VNPayConfig.getIpAddress(request);

                        long vnp_Amount = amount * 100;

                        Map<String, String> vnp_Params = new HashMap<>();

                        vnp_Params.put("vnp_Version", vnp_Version);
                        vnp_Params.put("vnp_Command", vnp_Command);
                        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);

                        vnp_Params.put("vnp_Amount",
                                        String.valueOf(vnp_Amount));

                        vnp_Params.put("vnp_CurrCode", "VND");

                        vnp_Params.put("vnp_TxnRef",
                                        vnp_TxnRef);

                        vnp_Params.put("vnp_OrderInfo",
                                        vnp_OrderInfo);

                        vnp_Params.put("vnp_OrderType",
                                        orderType);

                        vnp_Params.put("vnp_Locale", "vn");

                        vnp_Params.put("vnp_ReturnUrl",
                                        vnp_ReturnUrl);

                        vnp_Params.put("vnp_IpAddr",
                                        vnp_IpAddr);

                        Calendar cld = Calendar.getInstance(
                                        TimeZone.getTimeZone("Etc/GMT+7"));

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

                        String vnp_CreateDate = formatter.format(cld.getTime());

                        vnp_Params.put("vnp_CreateDate",
                                        vnp_CreateDate);

                        cld.add(Calendar.MINUTE, 15);

                        String vnp_ExpireDate = formatter.format(cld.getTime());

                        vnp_Params.put("vnp_ExpireDate",
                                        vnp_ExpireDate);

                        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());

                        Collections.sort(fieldNames);

                        StringBuilder hashData = new StringBuilder();

                        StringBuilder query = new StringBuilder();

                        boolean first = true;

                        for (String fieldName : fieldNames) {

                                String fieldValue = vnp_Params.get(fieldName);

                                if (fieldValue != null
                                                && !fieldValue.isEmpty()) {

                                        if (!first) {

                                                hashData.append('&');
                                                query.append('&');
                                        }

                                        hashData.append(fieldName);
                                        hashData.append('=');

                                        hashData.append(
                                                        URLEncoder.encode(
                                                                        fieldValue,
                                                                        StandardCharsets.US_ASCII.toString()));

                                        query.append(
                                                        URLEncoder.encode(
                                                                        fieldName,
                                                                        StandardCharsets.US_ASCII.toString()));

                                        query.append('=');

                                        query.append(
                                                        URLEncoder.encode(
                                                                        fieldValue,
                                                                        StandardCharsets.US_ASCII.toString()));

                                        first = false;
                                }
                        }

                        System.out.println("HASH DATA:");
                        System.out.println(hashData);

                        String vnp_SecureHash = VNPayConfig.hmacSHA512(
                                        vnp_HashSecret,
                                        hashData.toString());

                        System.out.println("SECURE HASH:");
                        System.out.println(vnp_SecureHash);

                        query.append("&vnp_SecureHash=");
                        query.append(vnp_SecureHash);

                        String paymentUrl = vnp_Url + "?" + query;

                        System.out.println(paymentUrl);

                        Map<String, String> response = new HashMap<>();

                        response.put("url", paymentUrl);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        e.printStackTrace();

                        return ResponseEntity.badRequest()
                                        .body("Cannot create VNPay URL");
                }
        }

        @GetMapping("/vnpay-return")
        public ResponseEntity<?> paymentReturn(
                        @RequestParam Map<String, String> params) {
                try {

                        String vnp_SecureHash = params.get("vnp_SecureHash");

                        params.remove("vnp_SecureHash");
                        params.remove("vnp_SecureHashType");

                        List<String> fieldNames = new ArrayList<>(params.keySet());

                        Collections.sort(fieldNames);

                        StringBuilder hashData = new StringBuilder();

                        boolean first = true;

                        for (String fieldName : fieldNames) {

                                String fieldValue = params.get(fieldName);

                                if (fieldValue != null
                                                && !fieldValue.isEmpty()) {

                                        if (!first) {
                                                hashData.append('&');
                                        }

                                        hashData.append(fieldName);
                                        hashData.append('=');

                                        hashData.append(
                                                        URLEncoder.encode(
                                                                        fieldValue,
                                                                        StandardCharsets.US_ASCII.toString()));

                                        first = false;
                                }
                        }

                        String signValue = VNPayConfig.hmacSHA512(
                                        vnp_HashSecret,
                                        hashData.toString());

                        if (!signValue.equals(vnp_SecureHash)) {

                                return ResponseEntity.badRequest()
                                                .body("INVALID CHECKSUM");
                        }

                        String responseCode = params.get("vnp_ResponseCode");

                        if ("00".equals(responseCode)) {

                                String txnRef = params.get("vnp_TxnRef");
                                System.out.println(txnRef);

                                Order order = orderService.findByTxnRef(txnRef);

                                orderService.confirmVNPayOrder(order.getId());

                                String htmlResponse = "<html>"
                                                + "<head><title>Kết quả thanh toán</title></head>"
                                                + "<body style='text-align: center; margin-top: 50px; font-family: Arial, sans-serif;'>"
                                                + "<h2 style='color: #2ecc71;'>🎉 Thanh toán đơn hàng thành công!</h2>"
                                                + "<p>Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.</p>"
                                                + "<br/>"
                                                + "<a href='https://koparion.vercel.app/' style='display: inline-block; padding: 10px 20px; "
                                                + "background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; "
                                                + "font-weight: bold;'>Quay lại trang chủ mua sắm</a>"
                                                + "</body>"
                                                + "</html>";
                                return ResponseEntity.ok(htmlResponse);
                        } else {
                                String htmlResponse = "<html>"
                                                + "<body style='text-align: center; margin-top: 50px; font-family: Arial, sans-serif;'>"
                                                + "<h2 style='color: #e74c3c;'>❌ Thanh toán thất bại hoặc đã bị hủy!</h2>"
                                                + "<br/>"
                                                + "<a href='http://localhost:3000/cart' style='display: inline-block; padding: 10px 20px; "
                                                + "background-color: #95a5a6; color: white; text-decoration: none; border-radius: 5px;'>Quay lại giỏ hàng</a>"
                                                + "</body>"
                                                + "</html>";
                                return ResponseEntity.ok(htmlResponse);
                        }

                } catch (Exception e) {

                        e.printStackTrace();

                        return ResponseEntity.badRequest()
                                        .body(e.getMessage());
                }
        }
}