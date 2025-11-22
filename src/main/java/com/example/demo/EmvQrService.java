package com.example.demo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.MAIData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;
import com.mastercard.mpqr.pushpayment.exception.FormatException;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Service
public class EmvQrService {

    // --------------------------------------------------------------------
    // Generate MPQR payload string
    // --------------------------------------------------------------------
    public String generateEmvQrString(String amount, String currency,
            String merchantName, String merchantCity,
            String country, String mai00, String mai01, String mai05) {

        PushPaymentData push = new PushPaymentData();

        try {
            push.setPayloadFormatIndicator("01");
            push.setValue("01", "12");
            push.setValue("02", "EMV");
            push.setValue("05", "0102CL0203CCM");

            // -----------------------------
            // MAI (tag 29)
            // -----------------------------
            String rootTag = "29";
            MAIData mai = new MAIData(rootTag);
            mai.setValue("00", mai00);
            String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new java.util.Date());

            mai.setValue("01", timestamp);
            mai.setValue("05", mai05);
            push.setMAIData(rootTag, mai);
            // 62
            AdditionalData additionalData = new AdditionalData();
            additionalData.setMobileNumber("123456");
            additionalData.setLoyaltyNumber("969696");

            push.setAdditionalData(additionalData);
            // Merchant info
            push.setMerchantName(merchantName);
            push.setMerchantCity(merchantCity);
            push.setCountryCode(country);
            push.setMerchantCategoryCode("1434"); // fixed
            push.setTransactionCurrencyCode(currency);
            push.setTransactionAmount(amount);
            return push.generatePushPaymentString();

        } catch (FormatException e) {
            throw new RuntimeException("QR Build Error: " + e.getMessage());

        }
    }

    public String xgenerateEmvQrString() {
        PushPaymentData push = new PushPaymentData();

        try {
            push.setPayloadFormatIndicator("01");
            push.setValue("01", "12");
            push.setValue("02", "EMV");
            push.setValue("05", "0102CL0203CCM");

            String rootTag = "29";
            MAIData mai = new MAIData(rootTag);
            mai.setValue("00", "100000010000331");
            mai.setValue("01", "240712101550");
            mai.setValue("05", "10000011");

            push.setMAIData(rootTag, mai);

            push.setMerchantName("CCM TEST MERCHANT");
            push.setMerchantCity("BEIRUT");
            push.setCountryCode("LB");
            push.setMerchantCategoryCode("1434");
            push.setTransactionCurrencyCode("840");
            push.setValue("54", "100.80");

            return push.generatePushPaymentString();

        } catch (FormatException e) {

            throw new RuntimeException("X QR Build Error: " + e.getMessage());
        }

    }

    // --------------------------------------------------------------------
    // Parse MPQR string
    // --------------------------------------------------------------------
    public String parseEmvQr(String qrString) {
        try {
            PushPaymentData data = Parser.parseWithValidationWarnings(qrString);
            return data.dumpData();
        } catch (FormatException e) {
            throw new RuntimeException("parseEmvQr: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // Convert payload to QR image bytes
    // --------------------------------------------------------------------
    public byte[] generateQrImage(String text) {

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);

            BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 300, 300);

            graphics.setColor(Color.BLACK);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    if (matrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }

            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return baos.toByteArray();

        } catch (WriterException | IOException e) {

            throw new RuntimeException("generateQrImage: " + e.getMessage());
        }

    }
}
