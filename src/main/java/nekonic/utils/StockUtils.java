package nekonic.utils;

public class StockUtils {

    // 주식 배당금을 계산하는 유틸리티 메소드
    public static double calculateDividend(double companyCapital, int totalShares, int playerShares) {
        if (totalShares == 0) return 0;
        return (companyCapital * playerShares) / totalShares;
    }

    // 주식 가격 평균 계산
    public static double calculateAveragePrice(int totalQuantity, double totalCost) {
        return totalQuantity == 0 ? 0 : totalCost / totalQuantity;
    }

    // 주식 수익률 계산
    public static double calculateProfitRate(double buyPrice, double sellPrice) {
        return (sellPrice - buyPrice) / buyPrice * 100;
    }
}
