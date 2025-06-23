package physicianconnect.persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSeeder {

    public static void seed(Connection connection, List<String> resourcePaths) {
        for (String path : resourcePaths) {
            executeSqlFromResource(path, connection);
        }
    }

    private static void executeSqlFromResource(String resourcePath, Connection connection) {
        InputStream input = DatabaseSeeder.class.getClassLoader().getResourceAsStream(resourcePath);

        if (input == null) {
            throw new RuntimeException("Seed file not found: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String sql = reader.lines().collect(Collectors.joining("\n"));
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute seed file: " + resourcePath, e);
            
        }
    }
}
