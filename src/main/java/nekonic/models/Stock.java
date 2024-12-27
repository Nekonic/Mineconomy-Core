package nekonic.models;

import java.sql.Timestamp;

public class Stock {
    private final String ticker;
    private final int currentPrice;
    private final String historicalPrices;
    private final int volume;
    private final Timestamp updatedAt;

    public Stock(String ticker, int currentPrice, String historicalPrices, int volume, Timestamp updatedAt) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.historicalPrices = historicalPrices;
        this.volume = volume;
        this.updatedAt = updatedAt;
    }

    public String getTicker() {
        return ticker;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public String getHistoricalPrices() {
        return historicalPrices;
    }

    public int getVolume() {
        return volume;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
