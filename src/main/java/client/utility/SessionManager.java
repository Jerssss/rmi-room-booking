package client.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();
    private static String currentSessionToken; // Holds the current session token

    public static void createSession(String sessionToken, String userId) {
        System.out.println("[SESSION] Creating session | Token: " + sessionToken + " | User: " + userId);
        activeSessions.put(sessionToken, userId);
        currentSessionToken = sessionToken; // Store the session token as the current session
    }

    public static String getUserId(String sessionToken) {
        return activeSessions.get(sessionToken);
    }

    public static void invalidateSession(String sessionToken) {
        activeSessions.remove(sessionToken);
        if (sessionToken.equals(currentSessionToken)) {
            currentSessionToken = null;
        }
    }

    public static boolean isValidSession(String sessionToken) {
        return activeSessions.containsKey(sessionToken);
    }

    public static String getStudentID() {
        // Return the user ID for the current session token, or null if none exists.
        if (currentSessionToken != null) {
            return getUserId(currentSessionToken);
        }
        return null;
    }
}
