package nekonic.DB;

public class Stock {

    private int quantity;  // 주식 수량
    private double price;  // 1주당 가격

    // 생성자
    public Stock(int quantity, double price) {
        this.quantity = quantity;
        this.price = price;
    }

    // 수량 getter
    public int getQuantity() {
        return quantity;
    }

    // 수량 setter
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 가격 getter
    public double getPrice() {
        return price;
    }

    // 가격 setter
    public void setPrice(double price) {
        this.price = price;
    }
}
