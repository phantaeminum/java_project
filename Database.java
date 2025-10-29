import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_FILE = "community_kitchen.db";
    private static final String INIT_SCRIPT = "init_db.sql";
    private static Database instance;
    private Connection connection;

    private Database() {
        try {
            Path dbPath = Paths.get(DB_FILE);
            boolean needsInit = !Files.exists(dbPath);
            
            // Create connection (will create DB file if it doesn't exist)
            // Ensure JDBC driver is available (will throw SQLException if not)
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            } catch (SQLException sqle) {
                System.err.println("Database connection error: " + sqle.getMessage());
                System.err.println("Make sure the SQLite JDBC driver is on the classpath (sqlite-jdbc).\n" +
                    "Download from https://github.com/xerial/sqlite-jdbc/releases and run java with -cp path\\to\\sqlite-jdbc.jar;.");
                throw sqle;
            }
            
            if (needsInit) {
                initializeDatabase();
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() {
        try {
            String initScript = Files.readString(Paths.get(INIT_SCRIPT));
            
            try (Statement stmt = connection.createStatement()) {
                // Split the script into individual statements
                for (String sql : initScript.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading init script: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database: " + e.getMessage());
            }
        }
    }
}