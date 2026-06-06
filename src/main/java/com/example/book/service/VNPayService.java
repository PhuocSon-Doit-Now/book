package com.example.book.service;

import com.example.book.config.VNPayConfig;
import com.example.book.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Value("${vnpay.vnp_Url}")
    private String vnpUrl;

    @Value("${vnpay.vnp_TmnCode}")
    private String tmnCode;

    @Value("${vnpay.vnp_HashSecret}")
    private String secretKey;

    @Value("${vnpay.vnp_ReturnUrl}")
    private String returnUrl;

    public String createPaymentUrl(Order order,
            HttpServletRequest request) {

        try {

            String vnpVersion = "2.1.0";
            String vnpCommand = "pay";
            String orderType = "other";

            String txnRef = order.getTxnRef();

            long amount = order.getTotalPrice()
                    .longValue() * 100;

            String ipAddr = VNPayConfig.getIpAddress(request);

            Calendar cld = Calendar.getInstance(
                    TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            String createDate = formatter.format(cld.getTime());

            cld.add(Calendar.MINUTE, 15);
            String expireDate = formatter.format(cld.getTime());

            Map<String, String> params = new TreeMap<>();

            params.put("vnp_Version", vnpVersion);
            params.put("vnp_Command", vnpCommand);
            params.put("vnp_TmnCode", tmnCode);
            params.put("vnp_Amount", String.valueOf(amount));
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", txnRef);
            params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
            params.put("vnp_OrderType", orderType);
            params.put("vnp_Locale", "vn");
            params.put("vnp_ReturnUrl", returnUrl);
            params.put("vnp_IpAddr", ipAddr);
            params.put("vnp_CreateDate", createDate);
            params.put("vnp_ExpireDate", expireDate);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            boolean first = true;

            for (Map.Entry<String, String> entry : params.entrySet()) {

                if (!first) {
                    hashData.append("&");
                    query.append("&");
                }

                hashData.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(
                                entry.getValue(),
                                StandardCharsets.UTF_8));

                query.append(URLEncoder.encode(
                        entry.getKey(),
                        StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(
                                entry.getValue(),
                                StandardCharsets.UTF_8));

                first = false;
            }

            String secureHash = VNPayConfig.hmacSHA512(
                    secretKey,
                    hashData.toString());

            query.append("&vnp_SecureHash=").append(secureHash);

            return vnpUrl + "?" + query;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}