public static String generateTcv(String amount, String currency, String deviceId, boolean isDynamic) {

    // --- Create dynamic timestamp only if needed ---
    String timestamp = "";
    if (isDynamic) {
        timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
                .format(new java.util.Date());
    }

    // --- Create random salt (always 4 digits) ---
    java.util.Random rnd = new java.util.Random();
    String salt = String.format("%04d", rnd.nextInt(10000));

    // --- Build the input string ---
    String input;
    if (isDynamic) {
        input = amount + "|" + currency + "|" + deviceId + "|" + timestamp + "|" + salt;
    } else {
        input = amount + "|" + currency + "|" + deviceId + "|";
    }

    try {
        // --- Compute SHA-256 ---
        java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = sha.digest(input.getBytes("UTF-8"));

        // --- Take first 8 bytes to form long ---
        long number = 0;
        for (int i = 0; i < 8; i++) {
            number = (number << 8) | (hash[i] & 0xFF);
        }

        // --- FIX: Force positive long (remove sign bit) ---
        long positive = number & 0x7FFFFFFFFFFFFFFFL;

        // --- Reduce to 6-digit TCV ---
        int tcv6 = (int)(positive % 1_000_000L);

        return String.format("%06d", tcv6);

    } catch (Exception e) {
        return "000000"; // fallback
    }
}
