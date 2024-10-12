package tests;

import nekonic.utils.StockUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// StockUtils 클래스의 유틸리티 메소드 테스트
public class StockUtilsTest {

    @Test
    public void testCalculateDividend() {
        // 배당금 계산이 올바르게 이루어지는지 테스트
        double dividend = StockUtils.calculateDividend(1000.0, 100, 10);

        // 1000 Mark 자본에서 10주에 대한 배당금이 100 Mark인지 확인
        assertEquals(100.0, dividend, 0.001);
    }

    @Test
    public void testCalculateAveragePrice() {
        // 평균 매입가 계산이 올바르게 이루어지는지 테스트
        double avgPrice = StockUtils.calculateAveragePrice(10, 500.0);

        // 총 500 Mark에 10주 구매했을 때 평균 매입가가 50 Mark인지 확인
        assertEquals(50.0, avgPrice, 0.001);
    }

    @Test
    public void testCalculateProfitRate() {
        // 주식 수익률 계산이 올바르게 이루어지는지 테스트
        double profitRate = StockUtils.calculateProfitRate(100.0, 150.0);

        // 100 Mark에 구매한 주식을 150 Mark에 팔았을 때 수익률이 50%인지 확인
        assertEquals(50.0, profitRate, 0.001);
    }
}
