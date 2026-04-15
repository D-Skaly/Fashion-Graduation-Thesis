package com.skaly.fashion_backend.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CursorUtils {

    /**
     * Encode cursor from timestamp and ID
     */
    public static String encodeCursor(long timestamp, String id) {
        String cursorData = timestamp + ":" + id;
        return Base64.getEncoder().encodeToString(cursorData.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode cursor to extract timestamp and ID
     */
    public static CursorData decodeCursor(String encodedCursor) {
        if (encodedCursor == null || encodedCursor.isEmpty()) {
            return null;
        }
        
        try {
            String cursorData = new String(Base64.getDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = cursorData.split(":");
            
            if (parts.length == 2) {
                long timestamp = Long.parseLong(parts[0]);
                String id = parts[1];
                return new CursorData(timestamp, id);
            }
        } catch (Exception e) {
            // Invalid cursor format
            return null;
        }
        
        return null;
    }

    public static class CursorData {
        private final long timestamp;
        private final String id;

        public CursorData(long timestamp, String id) {
            this.timestamp = timestamp;
            this.id = id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getId() {
            return id;
        }
    }
}
