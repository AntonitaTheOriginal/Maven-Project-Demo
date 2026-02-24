package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private HikariDataSource dataSource;
    
    public DatabaseService() {
        initDatabase();
    }
    
    private void initDatabase() {
        try {
            // Configure HikariCP connection pool
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:~/mydb;DB_CLOSE_DELAY=-1");
            config.setUsername("sa");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            
            dataSource = new HikariDataSource(config);
            
            // Create tables if they don't exist
            createTables();
            
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
        }
    }
    
    private void createTables() {
        String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INT AUTO_INCREMENT PRIMARY KEY,
                description VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                completed BOOLEAN DEFAULT FALSE
            )
        """;
        
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTasksTable);
            stmt.execute(createUsersTable);
            
            logger.info("Database tables created/verified");
        } catch (SQLException e) {
            logger.error("Error creating tables", e);
        }
    }
    
    // Task CRUD operations
    public void addTask(String description) {
        String sql = "INSERT INTO tasks (description) VALUES (?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, description);
            pstmt.executeUpdate();
            logger.info("Task added to database: {}", description);
            
        } catch (SQLException e) {
            logger.error("Error adding task to database", e);
        }
    }
    
    public List<String> getAllTasks() {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT id, description, completed FROM tasks ORDER BY id";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                String status = completed ? "[✓]" : "[ ]";
                tasks.add(String.format("%d. %s %s", id, status, desc));
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching tasks from database", e);
        }
        
        return tasks;
    }
    
    public void completeTask(int id) {
        String sql = "UPDATE tasks SET completed = TRUE WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                logger.info("Task {} marked as completed", id);
            } else {
                logger.warn("Task {} not found", id);
            }
            
        } catch (SQLException e) {
            logger.error("Error completing task", e);
        }
    }
    
    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int deleted = pstmt.executeUpdate();
            
            if (deleted > 0) {
                logger.info("Task {} deleted", id);
            } else {
                logger.warn("Task {} not found", id);
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting task", e);
        }
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection closed");
        }
    }
}