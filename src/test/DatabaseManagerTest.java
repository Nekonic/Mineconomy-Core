package tests;

import nekonic.DB.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// DatabaseManager의 주요 메소드 테스트
public class DatabaseManagerTest {

    private DatabaseManager databaseManager;

    @BeforeEach
    public void setup() {
        // DatabaseManager 인스턴스 생성 및 데이터베이스 초기화
        databaseManager = new DatabaseManager();
        databaseManager.setupDatabase();  // 데이터베이스 설정
    }

    @Test
    public void testGetBalance() {
        // 플레이어 자본을 업데이트한 후, 정상적으로 조회되는지 확인
        databaseManager.updateBalance("player", "testPlayer", 100.0);
        double balance = databaseManager.getBalance("player", "testPlayer");

        // 자본이 올바르게 저장되고 조회되는지 확인
        assertEquals(100.0, balance, 0.001);
    }

    @Test
    public void testIssueStock() {
        // 주식을 발행한 후, 주식이 정상적으로 데이터베이스에 저장되는지 확인
        databaseManager.issueStock("testCorp", 100, 50.0);

        // 기업의 주식 정보가 정상적으로 저장되었는지 확인
        assertFalse(databaseManager.getCorporationStocks("testCorp").isEmpty());
    }
}
