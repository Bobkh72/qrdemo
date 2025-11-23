package com.example.demo;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")

public class EmvQrController {

    private final EmvQrService service;

    public EmvQrController(EmvQrService service) {
        this.service = service;
    }

    // --------------------------------------------------------------------
    // 1. Generate EMV MPQR string
    // --------------------------------------------------------------------

@GetMapping("/generate")
public ResponseEntity<Map<String, String>> generateQr(
        @RequestParam String amount,
        @RequestParam String currency,
        @RequestParam String merchantName,
        @RequestParam String city,
        @RequestParam String country,
        @RequestParam String mai00,
        @RequestParam String mai01,
        @RequestParam String mai05
) {

    Map<String, String> resp = service.generateEmvQr(
            amount, currency, merchantName, city, country, mai00, mai01, mai05
    );

    return ResponseEntity.ok(resp);
}

    // --------------------------------------------------------------------
    // 2. Parse EMV QR string
    // --------------------------------------------------------------------
    @GetMapping("/parse")
    public ResponseEntity<String> parseQr(@RequestParam("data") String data) {
        String result = service.parseEmvQr(data);
        return ResponseEntity.ok(result);
    }

    // --------------------------------------------------------------------
    // 3. Generate PNG QR image
    // --------------------------------------------------------------------
    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(
            @RequestParam String amount,
            @RequestParam String currency,
            @RequestParam String merchantName,
            @RequestParam String city,
            @RequestParam String country,
            @RequestParam String mai00,
            @RequestParam String mai01,
            @RequestParam String mai05
    ) {
        String payload = service.generateEmvQrString(amount, currency, merchantName, city, country, mai00, mai01, mai05);
        byte[] img = service.generateQrImage(payload);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(img);
    }

}
