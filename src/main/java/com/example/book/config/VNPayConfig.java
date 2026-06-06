package com.example.book.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class VNPayConfig {

    public static String hmacSHA512(String key, String data) {

        try {

            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            hmac512.init(secretKey);

            byte[] hashBytes = hmac512.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (Exception e) {

            throw new RuntimeException("Error while hashing VNPay data", e);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null
                || ipAddress.isBlank()
                || "unknown".equalsIgnoreCase(ipAddress)) {

            ipAddress = request.getRemoteAddr();
        }

        // nhiều proxy
        if (ipAddress != null && ipAddress.contains(",")) {

            ipAddress = ipAddress.split(",")[0].trim();
        }

        // localhost IPv6
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)
                || "::1".equals(ipAddress)) {

            ipAddress = "127.0.0.1";
        }

        return ipAddress == null ? "127.0.0.1" : ipAddress;
    }
}