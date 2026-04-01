package com.servlet;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UserHistoryManager
 *
 * Handles persistent storage of login history using a simple CSV file.
 * The file is stored in the system's temp directory so it works on any OS
 * without needing special file permissions.
 *
 * CSV format: name,age,loginTime
 * Example:    Kenneth,22,2026-03-31 14:30:00
 *
 * All methods are synchronized to be thread-safe in case multiple users
 * log in at the same time.
 */
public class UserHistoryManager {

    // Store in system temp dir — always writable, survives Tomcat restarts
    private static final String FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "login_history.csv";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Represents a single login record.
    public static class LoginRecord {
        private final String name;
        private final int age;
        private final String loginTime;

        public LoginRecord(String name, int age, String loginTime) {
            this.name = name;
            this.age = age;
            this.loginTime = loginTime;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getLoginTime() {
            return loginTime;
        }
    }

    /**
     * Appends a new login record to the CSV file.
     * Called by LoginServlet every time a user successfully logs in.
     */
    public static synchronized void addRecord(String name, int age) {
        String loginTime = LocalDateTime.now().format(FORMATTER);
        // Sanitize name: remove commas to keep CSV valid
        String safeName = name.replace(",", " ");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) { // true = append mode
            writer.write(safeName + "," + age + "," + loginTime);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[UserHistoryManager] Failed to write record: " + e.getMessage());
        }
    }

    /**
     * Reads all login records from the CSV file.
     * Returns an empty list if the file doesn't exist yet.
     * Most recent logins appear first (list is reversed before returning).
     */
    public static synchronized List<LoginRecord> getAllRecords() {
        List<LoginRecord> records = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 3); // max 3 parts
                if (parts.length < 3) continue;      // skip malformed lines

                try {
                    String name = parts[0];
                    int age = Integer.parseInt(parts[1].trim());
                    String loginTime = parts[2];
                    records.add(new LoginRecord(name, age, loginTime));
                } catch (NumberFormatException ignored) {
                    // Skip any corrupted lines
                }
            }
        } catch (IOException e) {
            System.err.println("[UserHistoryManager] Failed to read records: " + e.getMessage());
        }

        // Reverse so most recent logins appear at the top
        java.util.Collections.reverse(records);
        return records;
    }

    /**
     * Returns the file path being used — useful for debugging.
     */
    public static String getFilePath() {
        return FILE_PATH;
    }
}
