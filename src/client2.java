// Client Application No. 2 (Java Version) - Avatar Word Match API Client
// Author: Austin B. Fenis

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

public class client2 {

    // API endpoint
    private static final String API_URL = "http://localhost/femb.php";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Scanner scanner = new Scanner(System.in);

    // ─────────────────────────────────────────────
    // MENU
    // ─────────────────────────────────────────────
    private static void displayMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("        AVATAR WORD MATCH API CLIENT");
        System.out.println("==================================================");
        System.out.println("1. GET    - View avatar");
        System.out.println("2. POST   - Create new avatar");
        System.out.println("3. PUT    - Update word & find matches");
        System.out.println("4. DELETE - Remove avatar");
        System.out.println("5. EXIT");
        System.out.println("--------------------------------------------------");
    }

    // ─────────────────────────────────────────────
    // 1. GET - View avatar
    // ─────────────────────────────────────────────
    private static void getAvatar() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        try {
            String url = API_URL + "?username=" + URLEncoder.encode(username, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, String> data = parseJson(response.body());
                if ("success".equals(data.get("status"))) {
                    System.out.println();
                    System.out.println("Avatar Found!");
                    System.out.println("   Username : " + data.get("username"));
                    System.out.println("   Word     : " + data.get("word"));
                    System.out.println("   Matches  : " + data.get("matches"));
                    System.out.println("   Rank     : " + data.get("rank"));
                } else {
                    System.out.println("\nError: " + data.getOrDefault("message", "Unknown error"));
                }
            } else {
                System.out.println("\nHTTP Error: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("\nRequest failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 2. POST - Create new avatar
    // ─────────────────────────────────────────────
    private static void createAvatar() {
        System.out.print("Enter new username (max 15 chars, letters/numbers/_.): ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter word (max 15 chars, default='apple'): ");
        String wordInput = scanner.nextLine().trim();
        String word = wordInput.isEmpty() ? "apple" : wordInput;

        try {
            String formBody = "username=" + URLEncoder.encode(username, "UTF-8")
                    + "&word=" + URLEncoder.encode(word, "UTF-8");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString(formBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, String> result = parseJson(response.body());
                if ("success".equals(result.get("status"))) {
                    System.out.println();
                    System.out.println("Avatar Created!");
                    System.out.println("   Username : " + result.get("username"));
                    System.out.println("   Word     : " + result.get("word"));
                    System.out.println("   Matches  : " + result.get("matches"));
                    System.out.println("   Rank     : " + result.get("rank"));
                } else {
                    System.out.println("\nError: " + result.get("message"));
                }
            } else {
                System.out.println("\nHTTP Error: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("\nRequest failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 3. PUT - Update word & find matches
    // ─────────────────────────────────────────────
    private static void updateAvatar() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter NEW word (max 15 chars): ");
        String word = scanner.nextLine().trim();

        try {
            // PUT request sends JSON body (mirrors Python client behaviour)
            String jsonBody = "{\"username\":\"" + escapeJson(username) + "\","
                    + "\"word\":\"" + escapeJson(word) + "\","
                    + "\"matches\":0}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .PUT(BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, String> result = parseJson(response.body());
                if ("success".equals(result.get("status"))) {
                    System.out.println();
                    System.out.println("Avatar Updated!");
                    System.out.println("   Username     : " + result.get("username"));
                    System.out.println("   Word         : " + result.get("word"));
                    System.out.println("   Total Matches: " + result.get("matches"));
                    System.out.println("   New Rank     : " + result.get("rank"));
                } else {
                    System.out.println("\nError: " + result.get("message"));
                }
            } else {
                System.out.println("\nHTTP Error: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("\nRequest failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 4. DELETE - Remove avatar
    // ─────────────────────────────────────────────
    private static void deleteAvatar() {
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine().trim();

        try {
            String url = API_URL + "?username=" + URLEncoder.encode(username, "UTF-8");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, String> result = parseJson(response.body());
                if ("success".equals(result.get("status"))) {
                    System.out.println();
                    System.out.println(result.getOrDefault("message", "User deleted"));
                    System.out.println("   Username: " + result.get("username"));
                } else {
                    System.out.println("\nError: " + result.get("message"));
                }
            } else {
                System.out.println("\nHTTP Error: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("\nRequest failed: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Minimal JSON parser (no external libraries)
    // Handles flat key-value JSON objects only.
    // ─────────────────────────────────────────────
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        // Strip surrounding braces
        json = json.trim();
        if (json.startsWith("{"))
            json = json.substring(1);
        if (json.endsWith("}"))
            json = json.substring(0, json.length() - 1);

        // Split on commas that are NOT inside quotes (simple approach)
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("^\"|\"$", "");
                String value = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, value);
            }
        }
        return map;
    }

    // Escape special characters for JSON strings
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ─────────────────────────────────────────────
    // Main
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println();
        System.out.println(" AVATAR WORD MATCH GAME CLIENT (Java)");
        System.out.println("Connecting to: " + API_URL);

        while (true) {
            displayMenu();
            System.out.print("Enter choice (1-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> getAvatar();
                case "2" -> createAvatar();
                case "3" -> updateAvatar();
                case "4" -> deleteAvatar();
                case "5" -> {
                    System.out.println("\nGoodbye!");
                    return;
                }
                default -> System.out.println("\nInvalid choice. Try again.");
            }

            System.out.println();
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
        }
    }
}
