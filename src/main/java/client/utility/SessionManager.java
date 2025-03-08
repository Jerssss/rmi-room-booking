package client.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    // Thread-safe map to store sessions: <SessionToken, UserID>
    public static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public static void createSession(String sessionToken, String userId) {
        System.out.println("[SESSION] Creating session | Token: " + sessionToken + " | User: " + userId);
        activeSessions.put(sessionToken, userId);
    }

    public static String getUserId(String sessionToken) {
        String userId = activeSessions.get(sessionToken);
        System.out.println("[SESSION] Lookup | Token: " + sessionToken + " | Found: " + userId);
        return userId;
    }

    public static void invalidateSession(String sessionToken) {
        System.out.println("[SESSION] Invalidating token: " + sessionToken);
        activeSessions.remove(sessionToken);
    }

    public static boolean isValidSession(String sessionToken) {
        boolean valid = activeSessions.containsKey(sessionToken);
        System.out.println("[SESSION] Validation | Token: " + sessionToken + " | Valid: " + valid);
        return valid;
    }
}