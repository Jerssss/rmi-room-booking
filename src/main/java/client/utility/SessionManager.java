package client.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public static void createSession(String sessionToken, String userId) {
        System.out.println("[SESSION] Creating session | Token: " + sessionToken + " | User: " + userId);
        activeSessions.put(sessionToken, userId);
    }

    public static String getUserId(String sessionToken) {
        return activeSessions.get(sessionToken);
    }

    public static void invalidateSession(String sessionToken) {
        activeSessions.remove(sessionToken);
    }

    public static boolean isValidSession(String sessionToken) {
        return activeSessions.containsKey(sessionToken);
    }
}
