package nekonic.DB;

import nekonic.Mineconomy;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseManager {

    private Connection connection;

    // 데이터베이스 초기화 및 테이블 생성
    public void setupDatabase() {
        try {
            // SQLite 데이터베이스 파일 생성
            String url = "jdbc:sqlite:" + Mineconomy.getInstance().getDataFolder() + "/mineconomy.db";
            connection = DriverManager.getConnection(url);

            // 테이블 생성
            createTables();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 테이블 생성 로직
    private void createTables() {
        try (Statement statement = connection.createStatement()) {

            // economy 테이블 (플레이어 및 기업 자본 관리용)
            String createEconomyTable = "CREATE TABLE IF NOT EXISTS economy ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "type TEXT NOT NULL,"    // player 또는 corporation
                    + "name TEXT NOT NULL,"
                    + "player_uuid TEXT,"      // 플레이어인 경우 UUID 저장 (플레이어만 해당)
                    + "balance DOUBLE DEFAULT 0.0"
                    + ");";

            // player_stocks 테이블 (플레이어의 주식 정보 저장)
            String createPlayerStocksTable = "CREATE TABLE IF NOT EXISTS player_stocks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "player_uuid TEXT NOT NULL,"  // 플레이어의 UUID
                    + "corporation TEXT NOT NULL,"  // 기업명
                    + "quantity INTEGER NOT NULL,"  // 보유 주식 수량
                    + "avg_price DOUBLE NOT NULL"   // 평균 매입 가격
                    + ");";

            // stocks 테이블 (기업의 발행 주식 관리)
            String createStocksTable = "CREATE TABLE IF NOT EXISTS stocks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "corporation TEXT NOT NULL,"  // 기업명
                    + "price DOUBLE NOT NULL,"      // 주식 가격
                    + "quantity INTEGER NOT NULL"   // 발행 주식 수량
                    + ");";

            // 쿼리 실행
            statement.executeUpdate(createEconomyTable);
            statement.executeUpdate(createPlayerStocksTable);
            statement.executeUpdate(createStocksTable);

            System.out.println("테이블 생성 성공!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("테이블 생성 실패: " + e.getMessage());
        }
    }

    // 플레이어 이름 목록을 조회 (자동완성)
    public List<String> getPlayerNames() {
        List<String> players = new ArrayList<>();
        String query = "SELECT name FROM economy WHERE type = 'player'";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                players.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    // 자본 조회
    public double getBalance(String type, String name) {
        String query = "SELECT balance FROM economy WHERE type = ? AND name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, type);  // player 또는 corporation
            statement.setString(2, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


    // 자본 업데이트
    public void updateBalance(String type, String name, double amount) {
        String updateSQL = "UPDATE economy SET balance = balance + ? WHERE type = ? AND name = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setDouble(1, amount);
            statement.setString(2, type);
            statement.setString(3, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 새로운 자본 기록 생성
    public void createEntryIfNotExists(String type, String name, String uuid) {
        String checkSQL = "SELECT * FROM economy WHERE type = ? AND name = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkSQL)) {
            statement.setString(1, type);
            statement.setString(2, name);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String insertSQL = "INSERT INTO economy (type, name, uuid, balance) VALUES (?, ?, ?, 0.0)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                    insertStatement.setString(1, type);
                    insertStatement.setString(2, name);
                    insertStatement.setString(3, uuid);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 기업 이름 목록 불러오기 (자동완성)
    public List<String> getCorporationNames() {
        List<String> corporations = new ArrayList<>();
        String query = "SELECT DISTINCT corporation FROM stocks";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                corporations.add(rs.getString("corporation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return corporations;
    }

    // 기업 주식 발행
    public void issueStock(String corporation, int quantity, double price) {
        String insertSQL = "INSERT INTO stocks (corporation, price, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, corporation);
            statement.setDouble(2, price);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 발행된 주식 정보 조회 (기업 기준)
    public List<Stock> getCorporationStocks(String corporation) {
        List<Stock> stockList = new ArrayList<>();
        String query = "SELECT * FROM stocks WHERE corporation = ? ORDER BY price ASC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, corporation);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                stockList.add(new Stock(rs.getInt("quantity"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockList;
    }

    // 플레이어가 보유한 모든 주식을 조회
    public List<PlayerStock> getPlayerAllStocks(String playerUuid) {
        List<PlayerStock> playerStocks = new ArrayList<>();
        String query = "SELECT * FROM player_stocks WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                playerStocks.add(new PlayerStock(
                        rs.getString("corporation"),
                        rs.getInt("quantity"),
                        rs.getDouble("avg_price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerStocks;
    }

    // 플레이어 주식 구매
    public void buyStock(String player, String corporation, int amount) {
        List<Stock> availableStocks = getCorporationStocks(corporation);
        int remainingAmount = amount;
        double totalCost = 0.0;

        for (Stock stock : availableStocks) {
            if (remainingAmount <= 0) break;

            int buyQuantity = Math.min(stock.getQuantity(), remainingAmount);
            double cost = buyQuantity * stock.getPrice();
            totalCost += cost;

            // 재고 차감
            updateStockQuantity(corporation, stock.getPrice(), -buyQuantity);
            remainingAmount -= buyQuantity;
        }

        if (remainingAmount > 0) {
            // 주식이 부족한 경우 처리
            // 예외 처리나 사용자 알림 추가 가능
        }

        // 플레이어 자본 차감 및 주식 추가 처리
        updatePlayerStock(player, corporation, amount - remainingAmount, totalCost);
    }

    // 플레이어 주식 업데이트
    private void updatePlayerStock(String player, String corporation, int quantity, double totalCost) {
        // 플레이어가 이미 해당 기업의 주식을 보유하고 있는지 확인
        String checkSQL = "SELECT * FROM player_stocks WHERE player = ? AND corporation = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkSQL)) {
            statement.setString(1, player);
            statement.setString(2, corporation);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // 이미 보유 중인 경우: 평균 가격을 업데이트하고 수량을 증가시킴
                int currentQuantity = rs.getInt("quantity");
                double currentAvgPrice = rs.getDouble("avg_price");

                int newQuantity = currentQuantity + quantity;
                double newAvgPrice = ((currentAvgPrice * currentQuantity) + totalCost) / newQuantity;

                String updateSQL = "UPDATE player_stocks SET quantity = ?, avg_price = ? WHERE player = ? AND corporation = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                    updateStatement.setInt(1, newQuantity);
                    updateStatement.setDouble(2, newAvgPrice);
                    updateStatement.setString(3, player);
                    updateStatement.setString(4, corporation);
                    updateStatement.executeUpdate();
                }

            } else {
                // 보유하고 있지 않으면 새로운 기록 생성
                String insertSQL = "INSERT INTO player_stocks (player, corporation, quantity, avg_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                    insertStatement.setString(1, player);
                    insertStatement.setString(2, corporation);
                    insertStatement.setInt(3, quantity);
                    insertStatement.setDouble(4, totalCost / quantity);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 주식 수량 업데이트
    private void updateStockQuantity(String corporation, double price, int quantityChange) {
        String updateSQL = "UPDATE stocks SET quantity = quantity + ? WHERE corporation = ? AND price = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            statement.setInt(1, quantityChange);
            statement.setString(2, corporation);
            statement.setDouble(3, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 플레이어가 보유한 주식을 판매
    public void sellStock(String player, String corporation, int amount, double price) {
        String insertSQL = "INSERT INTO sell_orders (player, corporation, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, player);
            statement.setString(2, corporation);
            statement.setInt(3, amount);
            statement.setDouble(4, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 기업 상장폐지
    public void delistCorporation(String corporation) {
        String deleteSQL = "DELETE FROM stocks WHERE corporation = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, corporation);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 연결 종료
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
