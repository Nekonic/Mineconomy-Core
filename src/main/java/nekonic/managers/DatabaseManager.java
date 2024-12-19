package nekonic.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static Connection connection;
    private final JavaPlugin plugin;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/economy.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }


    // ID 중복 체크 메서드
    public boolean isNameIdDuplicate(String nameId) {
        String query = "SELECT COUNT(*) FROM users WHERE name_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nameId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 새로운 사용자 추가 메서드
    public boolean addUser(String nameId, String uuid) {
        String query = "INSERT INTO users (name_id, uuid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nameId);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createTable() {
        try (InputStream inputStream = getClass().getResourceAsStream("/sql/create_tables.sql");
             Scanner scanner = new Scanner(inputStream)) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String sqlStatement = scanner.next().trim();
                if (!sqlStatement.isEmpty()) {
                    try (PreparedStatement stmt = connection.prepareStatement(sqlStatement)) {
                        stmt.execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBalance(String nameId) {
        String sql = "SELECT balance FROM users WHERE name_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(String nameId, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE name_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, nameId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 특정 ID가 플레이어인지 확인하는 메서드
    public boolean isPlayer(String nameId) {
        String query = "SELECT type FROM users WHERE name_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nameId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && "PLAYER".equals(rs.getString("type"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 특정 플레이어의 UUID를 가져오는 메서드
    public String getPlayerUUID(String nameId) {
        String query = "SELECT uuid FROM users WHERE name_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UUID 중복 확인 메서드
    public boolean isUUIDDuplicate(String uuid) {
        String query = "SELECT COUNT(*) FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getNameId(String uuid) {
        String query = "SELECT name_id FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // name_id가 없을 경우 null 반환
    }
}
