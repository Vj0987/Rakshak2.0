package com.sih.rakshak;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class ApkHashCalculator {

    public static class HashResult {
        private final String sha256Hash;
        private final String filePath;

        public HashResult(String sha256Hash, String filePath) {
            this.sha256Hash = sha256Hash;
            this.filePath = filePath;
        }

        public String getSha256Hash() {
            return sha256Hash;
        }

        public String getFilePath() {
            return filePath;
        }
    }

    private static String calculateSHA256(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(filePath);

            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();

            byte[] bytes = digest.digest();

            // Convert the byte array to a hexadecimal format
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                hashStringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return hashStringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashResult getApkSHA256(String filePath) {
        String sha256Hash = calculateSHA256(filePath);
        return new HashResult(sha256Hash, filePath);
    }
}
