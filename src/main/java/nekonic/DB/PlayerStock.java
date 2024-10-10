package nekonic.DB;

public class PlayerStock {

    private String corporation;  // 기업명
    private int quantity;    // 플레이어가 소유한 주식 수량
    private double avgPrice; // 평균 매입 단가

    // 생성자
    public PlayerStock(String corporation, int quantity, double avgPrice) {
        this.corporation = corporation;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    // 기업명 getter
    public String getCorporation() {
        return corporation;
    }

    // 수량 getter
    public int getQuantity() {
        return quantity;
    }

    // 수량 setter
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 평균 매입 단가 getter
    public double getAvgPrice() {
        return avgPrice;
    }

    // 평균 매입 단가 setter
    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }
}
