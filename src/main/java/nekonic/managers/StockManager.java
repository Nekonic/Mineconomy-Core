package nekonic.managers;

import nekonic.models.Stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockManager {

    // 주식 조회
    public static Stock getStock(String ticker) {
        String query = "SELECT * FROM stocks WHERE ticker = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, ticker);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Stock(
                        rs.getString("ticker"),
                        rs.getDouble("current_price"),
                        rs.getString("historical_prices"),
                        rs.getInt("volume"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모든 주식 조회
    public static List<Stock> getAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM stocks";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(new Stock(
                        rs.getString("ticker"),
                        rs.getDouble("current_price"),
                        rs.getString("historical_prices"),
                        rs.getInt("volume"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    // 주식 생성
    public static boolean createStock(Stock stock) {
        String query = "INSERT INTO stocks (ticker, current_price, historical_prices, volume, updated_at) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, stock.getTicker());
            stmt.setDouble(2, stock.getCurrentPrice());
            stmt.setString(3, stock.getHistoricalPrices());
            stmt.setInt(4, stock.getVolume());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
